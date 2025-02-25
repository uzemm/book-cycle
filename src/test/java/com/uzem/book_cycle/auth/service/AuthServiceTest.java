package com.uzem.book_cycle.auth.service;

import com.uzem.book_cycle.auth.dto.SignUpRequestDTO;
import com.uzem.book_cycle.auth.email.repository.EmailVerificationRepository;
import com.uzem.book_cycle.auth.email.service.EmailService;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.dto.MemberDTO;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.member.type.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.uzem.book_cycle.member.type.MemberStatus.PENDING;
import static com.uzem.book_cycle.member.type.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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
        given(passwordEncoder.encode(request.getPassword())).willReturn("12345678");

        //when
        MemberDTO memberDTO = authService.signUp(request);

        //then
        assertThat(memberDTO.getEmail()).isEqualTo(request.getEmail());
        //호출여부
        verify(emailRepository).save(any());
        verify(emailService).sendVerification(anyString(), anyString());

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

}





