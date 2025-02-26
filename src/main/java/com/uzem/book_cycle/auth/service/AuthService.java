package com.uzem.book_cycle.auth.service;

import com.uzem.book_cycle.auth.dto.SignUpRequestDTO;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.auth.email.entity.EmailVerification;
import com.uzem.book_cycle.auth.email.repository.EmailVerificationRepository;
import com.uzem.book_cycle.auth.email.service.EmailService;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.dto.MemberDTO;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.member.type.MemberErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.uzem.book_cycle.member.type.MemberStatus.ACTIVE;
import static com.uzem.book_cycle.member.type.MemberStatus.PENDING;
import static com.uzem.book_cycle.member.type.Role.USER;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailRepository;

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
                .refreshToken("")
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
        String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
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
    public EmailVerificationResponseDTO verifyCheck(String email, String verificationCode) throws MemberException {
        //인증코드를 찾는다
        EmailVerification emailVerification = emailRepository.findByEmailAndVerificationCode(email, verificationCode)
                .orElseThrow(() -> new MemberException(MemberErrorCode.EMAIL_VERIFICATION_CODE_INVALID));

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

}
