package com.uzem.book_cycle.auth.service;

import com.uzem.book_cycle.auth.dto.SignUpRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpResponseDTO;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.auth.email.entity.EmailVerification;
import com.uzem.book_cycle.auth.email.repository.EmailVerificationRepository;
import com.uzem.book_cycle.auth.email.service.EmailService;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.member.type.MemberErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.uzem.book_cycle.member.type.MemberErrorCode.*;
import static com.uzem.book_cycle.member.type.MemberStatus.ACTIVE;
import static com.uzem.book_cycle.member.type.MemberStatus.PENDING;
import static com.uzem.book_cycle.member.type.Role.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailRepository;

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
                .point(0L)
                .isDeleted(false)
                .socialId(null)
                .socialType(null)
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
}
