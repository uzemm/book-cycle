package com.uzem.book_cycle.auth.service;

import com.uzem.book_cycle.auth.dto.LoginRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpRequestDTO;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.auth.email.entity.EmailVerification;
import com.uzem.book_cycle.auth.email.repository.EmailVerificationRepository;
import com.uzem.book_cycle.auth.email.service.EmailService;
import com.uzem.book_cycle.security.token.TokenDTO;
import com.uzem.book_cycle.security.token.TokenProvider;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.exception.TokenException;
import com.uzem.book_cycle.member.dto.MemberDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
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
    public MemberDTO signUp(SignUpRequestDTO request) {
        validationSignUp(request);

        Member member = Member.builder()
                .email(request.getEmail())
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(USER)
                .status(PENDING)
                .rentalCnt(0)
                .point(0L)
                .isDeleted(false)
                .socialId(null)
                .socialType(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        //비밀번호 암호화
        member.encodePassword(passwordEncoder, request.getPassword());
        memberRepository.save(member); //회원저장

        //인증코드 생성
        String verificationCode = UUID.randomUUID().toString().replace("-", "")
                .substring(0, 8).toUpperCase();
        EmailVerification emailVerification  = EmailVerification.builder()
                .member(member)
                .email(request.getEmail())
                .verificationCode(verificationCode)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        emailRepository.save(emailVerification);

        //이메일 전송
        emailService.sendVerification(member.getEmail(), verificationCode);

        return MemberDTO.fromEntity(member);
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
    public EmailVerificationResponseDTO verifyCheck(String email, String verificationCode)
            throws MemberException {
        //인증코드를 찾는다
        EmailVerification emailVerification =
                emailRepository.findByEmailAndVerificationCode(email, verificationCode)
                .orElseThrow(() -> new MemberException
                        (MemberErrorCode.EMAIL_VERIFICATION_CODE_INVALID));

        validationVerifyEmail(emailVerification);

        //회원 가져옴
        Member member = emailVerification.getMember();

        //이미 인증된 상태
        if(member.getStatus() == ACTIVE){
            throw new MemberException(MemberErrorCode.EMAIL_ALREADY_VERIFIED);
        }
        //회원상태 변경
        member.setStatus(ACTIVE);
        memberRepository.save(member);

        //이메일 인증 완료
        emailVerification.setVerified(true);
        emailRepository.save(emailVerification);

        return EmailVerificationResponseDTO.from(member, emailVerification);

    }

    private static void validationVerifyEmail(EmailVerification emailVerification) {
        //인증코드 만료
        if(emailVerification.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new MemberException(MemberErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED);
        }
    }


    public TokenDTO login(LoginRequestDTO loginRequestDTO) {
        log.debug(" 로그인 요청: {}", loginRequestDTO.getEmail());
        //회원 조회
        Member member = memberRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new MemberException(INCORRECT_ID_OR_PASSWORD));
        log.debug(" 회원 조회 성공: {}", member.getEmail());
        //회원 상태 조회
        if(member.getStatus() == PENDING){
            log.debug(" 이메일 미인증 회원 로그인 시도: {}", member.getEmail());
            throw new MemberException(EMAIL_NOT_VERIFIED);
        }

        //사용자 인증
        log.debug(" 사용자 인증 시도: {}", loginRequestDTO.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );
        log.debug(" 사용자 인증 성공: {}", authentication.getName());

        // JWT 토큰 생성 (엑세스 30분, 리프레시 2주)
        TokenDTO tokenDto = tokenProvider.generateTokenDto(authentication);
        log.debug(" JWT 토큰 생성 완료: {}", tokenDto.getAccessToken());

        // Redis에 기존 리프레시 토큰이 있으면 삭제 (보안 강화)
        redisUtil.delete(loginRequestDTO.getEmail());

        // Redis에 저장 (자동 만료 설정)
        redisUtil.save(loginRequestDTO.getEmail(), tokenDto.getRefreshToken());
        log.debug(" Redis에 Refresh Token 저장 완료: {}", loginRequestDTO.getEmail());

        return tokenDto;
    }

    @Transactional
    public void logout(String accessToken) {
        // tokenProvider에서 email 정보 가져옴
        String email = tokenProvider.getAuthentication(accessToken).getName();

        // 로그아웃 시 리프레시 토큰 삭제 (로그인 유지 X)
        redisUtil.delete(email);

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
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new TokenException(INVALID_TOKEN);
        }
        // 리프레시 토큰에서 클레임 추출
        Claims claims = tokenProvider.getClaimsFromValidToken(refreshToken);
        // 토큰의 타입 리프레시 토큰이 맞는지
        if (!Boolean.TRUE.equals(claims.get("isRefreshToken"))) {
            throw new TokenException(TokenErrorCode.NOT_A_REFRESH_TOKEN);
        }

        // DB에서 사용자 정보 조회 (추가 검증)
        String email = claims.getSubject();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 현재 시간과 비교하여 만료 여부 체크
        Date expirationDate = claims.getExpiration();
        long expirationTime = expirationDate.getTime();
        if (System.currentTimeMillis() > expirationTime) {
            throw new TokenException(EXPIRED_TOKEN);
        }

        // Redis에서 해당 사용자의 리프레시 토큰이 존재하는지 확인 (보안 강화)
        String storeRefreshToekn = redisUtil.get(email);
        if(storeRefreshToekn == null || !storeRefreshToekn.equals(refreshToken)){
            throw new TokenException(INVALID_TOKEN);
        }

        return tokenProvider.reissueAccessToken(refreshToken);
    }

}
