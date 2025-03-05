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
import com.uzem.book_cycle.member.type.MemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.uzem.book_cycle.member.type.MemberErrorCode.*;
import static com.uzem.book_cycle.member.type.MemberStatus.*;
import static com.uzem.book_cycle.member.type.MemberStatus.PENDING;
import static com.uzem.book_cycle.member.type.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EmailVerificationRepository emailRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공")
    void successSignUp() {
        //given
        SignUpRequestDTO request = createSignUpRequest();
        given(memberRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(memberRepository.existsByPhone(request.getPhone())).willReturn(false);
        given(memberRepository.save(any(Member.class))).willReturn(createMember(request));
        given(passwordEncoder.encode(request.getPassword())).willReturn("encodePassword");

        //when
        SignUpResponseDTO signUpResponseDTO = authService.signUp(request);

        //then
        assertThat(signUpResponseDTO.getEmail()).isEqualTo(request.getEmail());
        //호출여부
        verify(emailRepository, times(1)).save(any());
        verify(emailService, times(1)).sendVerification(anyString(), anyString());

    }

    @Test
    @DisplayName("회원가입 - 이메일 중복")
    void signUp_EmailAlreadyInUse() {
        //given
        SignUpRequestDTO request = createSignUpRequest();
        given(memberRepository.existsByEmail(request.getEmail())).willReturn(true);

        //when

        MemberException exception = assertThrows(MemberException.class,
                () -> authService.signUp(request));
        //then
        assertEquals(MemberErrorCode.EMAIL_ALREADY_IN_USE, exception.getMemberErrorCode());
    }

    @Test
    @DisplayName("회원가입 - 전화번호 중복")
    void signUp_PhoneAlreadyInUse() {
        //given
        SignUpRequestDTO request = createSignUpRequest();
        given(memberRepository.existsByPhone(request.getPhone())).willReturn(true);

        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> authService.signUp(request));
        //then
        assertEquals(MemberErrorCode.PHONE_ALREADY_IN_USE, exception.getMemberErrorCode());
    }

    private SignUpRequestDTO createSignUpRequest() {
        return SignUpRequestDTO.builder()
                .email("test@uzem.com")
                .password("12345678")
                .phone("01012345678")
                .name("테스트 유저")
                .address("서울시 강남구")
                .build();
    }

    private Member createMember(SignUpRequestDTO request) {
        return Member.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .name(request.getName())
                .address(request.getAddress())
                .role(USER)
                .status(PENDING)
                .build();
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void success_verifyCheck() {
        //given
        String email = "test@uzem.com";
        String verificationCode = "12345678";

        Member member = Member.builder()
                .id(1L)
                .email(email)
                .status(PENDING)
                .build();

        EmailVerification emailVerification = EmailVerification.builder()
                .member(member)
                .email(email)
                .verificationCode(verificationCode)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        given(emailRepository.findByEmailAndVerificationCode(email, verificationCode))
                .willReturn(Optional.of(emailVerification));

        //when
        EmailVerificationResponseDTO responseDTO = authService.verifyCheck(email, verificationCode);

        //then
        assertThat(responseDTO.getEmail()).isEqualTo(email);
        assertThat(responseDTO.getStatus()).isEqualTo(ACTIVE);
        assertThat(member.getStatus()).isEqualTo(ACTIVE);

        verify(emailRepository, times(1)).delete(emailVerification);
    }

    @Test
    @DisplayName("이메일 인증 실패 - 잘못된 인증코드")
    void fail_verifyCheck_invalid() {
        //given
        String email = "test@uzem.com";
        String verificationCode = "12345678";
        given(emailRepository.findByEmailAndVerificationCode(email, verificationCode))
                .willReturn(Optional.empty());
        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> authService.verifyCheck(email, verificationCode));
        //then
        assertEquals(EMAIL_VERIFICATION_CODE_INVALID, exception.getMemberErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 인증코드 만료")
    void fail_verifyCheck_expired() {
        //given
        String email = "test@uzem.com";
        String verificationCode = "12345678";

        Member member = Member.builder()
                .id(1L)
                .email(email)
                .status(MemberStatus.PENDING)
                .build();

        EmailVerification expiredVerification = EmailVerification.builder()
                .member(member)
                .email(email)
                .verificationCode(verificationCode)
                .expiresAt(LocalDateTime.now().minusMinutes(10)) // 만료된 코드
                .build();

        given(emailRepository.findByEmailAndVerificationCode(email, verificationCode))
                .willReturn(Optional.of(expiredVerification));
        //when

        MemberException exception = assertThrows(MemberException.class,
                () -> authService.verifyCheck(email, verificationCode));
        //then
        assertEquals(EMAIL_VERIFICATION_CODE_EXPIRED, exception.getMemberErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 이미 인증 완료")
    void fail_verifyCheck_already() {
        //given
        String email = "test@uzem.com";
        String verificationCode = "12345678";

        Member member = Member.builder()
                .id(1L)
                .email(email)
                .status(ACTIVE) //인증완료
                .build();

        EmailVerification expiredVerification = EmailVerification.builder()
                .member(member)
                .email(email)
                .verificationCode(verificationCode)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        given(emailRepository.findByEmailAndVerificationCode(email, verificationCode))
                .willReturn(Optional.of(expiredVerification));
        //when

        MemberException exception = assertThrows(MemberException.class,
                () -> authService.verifyCheck(email, verificationCode));
        //then
        assertEquals(EMAIL_ALREADY_VERIFIED, exception.getMemberErrorCode());
    }

}





