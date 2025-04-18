package com.uzem.book_cycle.member.service;

import com.uzem.book_cycle.auth.email.entity.EmailVerification;
import com.uzem.book_cycle.auth.email.repository.EmailVerificationRepository;
import com.uzem.book_cycle.auth.email.service.EmailService;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.dto.*;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.redis.RedisUtil;
import com.uzem.book_cycle.security.SecurityContextService;
import com.uzem.book_cycle.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.uzem.book_cycle.member.type.MemberErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final SecurityContextService securityContextService;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final TokenProvider tokenProvider;
    private final EmailService emailService;
    private final EmailVerificationRepository emailRepository;

    // 내정보 조회
    public MemberResponseDTO getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));

        return MemberResponseDTO.from(member);
    }

    @Transactional
    public MemberResponseDTO updateMyInfo(
            Long memberId, UpdateInfoRequestDTO requestDTO) {

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));

        member.updateMyInfo(requestDTO.getPhone(), requestDTO.getAddress());

        // SecurityContext 인증 정보 업데이트
        securityContextService.updateAuthentication(member);

        return MemberResponseDTO.from(member);
    }

    @Transactional
    public void updatePassword
            (Long memberId, UpdatePasswordRequestDTO requestDTO,
             String accessToken) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));

        // 현재 비밀번호 확인
        validationUpdatePassword(requestDTO, member);

        String newPassword = passwordEncoder.encode(requestDTO.getNewPassword());
        member.updatePassword(newPassword);

        // 리프레시 토큰 삭제
        redisUtil.delete("refreshToken:" + memberId);

        long expiration = tokenProvider.getExpiration(accessToken);
        if(expiration > 0){
            redisUtil.setBlackList("blacklist:" + accessToken, "access_token", expiration);
        }

        // SecurityContext 초기화 (현재 세션 인증 정보 삭제)
        SecurityContextHolder.clearContext();

        log.info("비밀번호 변경 후 로그아웃 완료: {}", memberId);
    }

    private void validationUpdatePassword(UpdatePasswordRequestDTO requestDTO, Member member) {
        // 현재 비밀번호 확인
        if(!passwordEncoder.matches(requestDTO.getCurrentPassword(), member.getPassword())) {
            throw new MemberException(INCORRECT_PASSWORD);
        }
        // 현재 비밀번호와 새 비밀번호 동일
        if(passwordEncoder.matches(requestDTO.getNewPassword(), member.getPassword())) {
            throw new MemberException(SAME_AS_CURRENT_PASSWORD);
        }

        // 새로운 비밀번호 일치 확인
        if(!Objects.equals(requestDTO.getNewPassword(), requestDTO.getConfirmPassword())) {
            throw new MemberException(CONFIRM_PASSWORD_MISMATCH);
        }
    }

    // 이메일 변경 요청
    public void  updateEmail(Long memberId, UpdateEmailRequestDTO requestDTO) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));

        if(memberRepository.findByEmail(requestDTO.getNewEmail()).isPresent()){
            throw new MemberException(DUPLICATE_EMAIL);
        }

        EmailVerification emailVerification = createEmailVerification(member, requestDTO);
        emailRepository.save(emailVerification);

        emailService.sendVerification(requestDTO.getNewEmail(), emailVerification.getVerificationCode());
    }

    //인증코드 생성
    private EmailVerification createEmailVerification(
            Member member, UpdateEmailRequestDTO requestDTO) {
        String verificationCode = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 8).toUpperCase();

        return EmailVerification.builder()
                .member(member)
                .email(requestDTO.getNewEmail())
                .verificationCode(verificationCode)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
    }

    // 인증코드 확인
    public MemberResponseDTO UpdateEmailCheck(
            String email, String verificationCode) {
        //인증코드를 찾는다
        EmailVerification emailVerification = emailRepository.
                findByEmailAndVerificationCode(email, verificationCode)
                .orElseThrow(() -> new MemberException(EMAIL_VERIFICATION_CODE_INVALID));

        validateEmailVerification(emailVerification);

        //회원 가져옴
        Member member = emailVerification.getMember();

        //회원상태 변경
        member.updateEmail(emailVerification.getEmail());

        //이메일 인증 완료
        emailVerification.verified();
        emailRepository.save(emailVerification);

        //인증 성공 시 데이터 삭제
        emailRepository.delete(emailVerification);

        return MemberResponseDTO.from(member);
    }

    // 인증 코드 만료 확인
    private static void validateEmailVerification(EmailVerification emailVerification) {
        if(emailVerification.isExpired()){
            throw new MemberException(EMAIL_VERIFICATION_CODE_EXPIRED);
        }
    }
}
