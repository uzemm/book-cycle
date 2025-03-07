package com.uzem.book_cycle.auth.service;

import com.uzem.book_cycle.auth.dto.LoginRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpResponseDTO;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.auth.email.entity.EmailVerification;
import com.uzem.book_cycle.auth.email.repository.EmailVerificationRepository;
import com.uzem.book_cycle.auth.email.service.EmailService;
import com.uzem.book_cycle.security.token.TokenDTO;
import com.uzem.book_cycle.security.token.TokenProvider;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.exception.TokenException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.member.type.MemberErrorCode;
import com.uzem.book_cycle.redis.RedisUtil;
import com.uzem.book_cycle.security.token.TokenErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.uzem.book_cycle.member.type.MemberErrorCode.*;
import static com.uzem.book_cycle.member.type.MemberStatus.ACTIVE;
import static com.uzem.book_cycle.member.type.MemberStatus.PENDING;
import static com.uzem.book_cycle.member.type.Role.USER;
import static com.uzem.book_cycle.security.token.TokenErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;

    @Transactional
    public SignUpResponseDTO signUp(SignUpRequestDTO request) {
        validationSignUp(request);

        String password = passwordEncoder.encode(request.getPassword());

        Member member = Member.builder()
                .email(request.getEmail())
                .password(password)
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(USER)
                .status(PENDING)
                .rentalCnt(0)
                .refreshToken("")
                .point(0L)
                .isDeleted(false)
                .socialId(null)
                .socialType(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        memberRepository.save(member); //회원저장

        //이메일 인증 정보 저장
        EmailVerification emailVerification = createEmailVerification(member);
        emailRepository.save(emailVerification);

        //트랜잭션 종료 후 이메일 전송 (비동기 실행)
        sendVerificationEmailAsync(member, emailVerification.getVerificationCode());

        return SignUpResponseDTO.from(member);
    }

    //인증코드 생성
    private EmailVerification createEmailVerification(Member member) {
        String verificationCode = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 8).toUpperCase();

        return EmailVerification.builder()
                .member(member)
                .email(member.getEmail())
                .verificationCode(verificationCode)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
    }

    private void validationSignUp(SignUpRequestDTO request) {
        //이메일 중복검사
        if(memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberException(MemberErrorCode.EMAIL_ALREADY_IN_USE);
        }
        //전화번호 중복검사
        if(memberRepository.existsByPhone(request.getPhone())) {
            throw new MemberException(MemberErrorCode.PHONE_ALREADY_IN_USE);
        }
    }

    @Transactional
    public EmailVerificationResponseDTO verifyCheck(String email, String verificationCode) {
        //인증코드를 찾는다
        EmailVerification emailVerification = emailRepository.
                findByEmailAndVerificationCode(email, verificationCode)
                .orElseThrow(() -> new MemberException(EMAIL_VERIFICATION_CODE_INVALID));

        validateEmailVerification(emailVerification);

        //회원 가져옴
        Member member = emailVerification.getMember();

        //이미 인증된 상태
        if(member.getStatus() == ACTIVE){
            throw new MemberException(MemberErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        //회원상태 변경
        member.activateMember();
        memberRepository.save(member);

        //이메일 인증 완료
        emailVerification.verified();
        emailRepository.save(emailVerification);

        //인증 성공 시 데이터 삭제
        emailRepository.delete(emailVerification);

        return EmailVerificationResponseDTO.from(member, emailVerification);
    }

    // 인증 코드 만료 확인
    private static void validateEmailVerification(EmailVerification emailVerification) {
        if(emailVerification.isExpired()){
            throw new MemberException(EMAIL_VERIFICATION_CODE_EXPIRED);
        }
    }

    @Async // 비동기 실행
    public void sendVerificationEmailAsync(Member member, String verificationCode) {
        try{
            emailService.sendVerification(member.getEmail(), verificationCode);
        } catch (MemberException e) {
            log.error("이메일 전송 실패 : {}", member.getEmail(), e);
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteExpiredEmailVerifications(){
        LocalDateTime localDateTime = LocalDateTime.now();
        emailRepository.deleteAllByExpiresAtBefore(localDateTime);
    }

    public TokenDTO login(LoginRequestDTO loginRequestDTO) {
        log.debug(" 로그인 요청: {}", loginRequestDTO.getEmail());
        //회원 조회
        Member member = memberRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new MemberException(INCORRECT_ID_OR_PASSWORD));

        validationLogin(loginRequestDTO, member);

        //사용자 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );
        log.debug(" 사용자 인증 성공: {}", authentication.getName());

        // JWT 토큰 생성 (엑세스 30분, 리프레시 2주)
        TokenDTO tokenDto = tokenProvider.generateTokenDto(authentication);
        log.debug(" JWT 토큰 생성 완료: {}", tokenDto.getAccessToken());

        // Redis에 기존 리프레시 토큰이 있으면 삭제 (보안 강화)
        redisUtil.delete("refreshToken:" + member.getId());

        // Redis에 저장 (자동 만료 설정)
        redisUtil.save("refreshToken:" + member.getId(), tokenDto.getRefreshToken());
        log.debug(" Redis에 Refresh Token 저장 완료: {}", loginRequestDTO.getEmail());

        return tokenDto;
    }

    private void validationLogin(LoginRequestDTO loginRequestDTO, Member member) {
        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())){
            throw new MemberException(INCORRECT_ID_OR_PASSWORD);
        }
        //회원 상태 조회
        if(member.getStatus() == PENDING){
            throw new MemberException(EMAIL_NOT_VERIFIED);
        }
    }

    public void logout(String accessToken) {
        // tokenProvider에서 email 정보 가져옴
        Long memberId = Long.valueOf(tokenProvider.getAuthentication(accessToken).getName());

        // 로그아웃 시 리프레시 토큰 삭제 (로그인 유지 X)
        redisUtil.delete("refreshToken:" + memberId);

        // Access Token blacklist에 등록하여 만료시키기
        long expiration = tokenProvider.getExpiration(accessToken);
        if(expiration > 0){
            redisUtil.setBlackList(accessToken, "access_token", expiration);
        }
    }

    //엑세스 재발급
    @Transactional
    public TokenDTO reissueAccessToken(String refreshToken) {
         // 리프레시 토큰 검증
        tokenProvider.validateToken(refreshToken);

        // 리프레시 토큰에서 클레임 추출
        Claims claims = tokenProvider.getClaimsFromValidToken(refreshToken);
        // 토큰의 타입 리프레시 토큰이 맞는지
        if (!Boolean.TRUE.equals(claims.get("isRefreshToken"))) {
            throw new TokenException(TokenErrorCode.NOT_A_REFRESH_TOKEN);
        }

        // DB에서 사용자 정보 조회 (추가 검증)
        Long memberId = Long.valueOf(claims.getSubject());
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // Redis에서 해당 사용자의 리프레시 토큰이 존재하는지 확인 (보안 강화)
        String storeRefreshToken = redisUtil.get("refreshToken:" + memberId);
        log.debug("🔍 Redis에서 가져온 Refresh Token: {}", storeRefreshToken);

        if(storeRefreshToken == null || !storeRefreshToken.equals(refreshToken)){
            throw new TokenException(INVALID_TOKEN);
        }

        //새로운 엑세스 토큰 발급
        TokenDTO tokenDTO = tokenProvider.reissueAccessToken(refreshToken);

        // 인증 정보 초기화
        SecurityContextHolder.clearContext();

        // 새로운 엑세스 토큰으로 인증 정보 생성 및 설정
        Authentication authentication = tokenProvider.getAuthentication(tokenDTO.getAccessToken());
        SecurityContextHolder.getContext().setAuthentication(authentication);


        return tokenDTO;
    }

}
