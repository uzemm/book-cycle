package com.uzem.book_cycle.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzem.book_cycle.auth.dto.SignUpRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpResponseDTO;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.auth.service.AuthService;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationRequestDTO;
import com.uzem.book_cycle.auth.email.service.EmailService;
import com.uzem.book_cycle.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.uzem.book_cycle.member.type.MemberStatus.ACTIVE;
import static com.uzem.book_cycle.member.type.MemberStatus.PENDING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private MemberRepository repository;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private SecurityFilterChain securityFilterChain;

    @MockitoBean
    JpaMetamodelMappingContext mapping;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser(username = "testuser", roles = {"USER"}) //가짜 사용자 추가
    void successSignUp() throws Exception {
        //given
        SignUpRequestDTO requestDTO = SignUpRequestDTO.builder()
                .email("test@uzem.com")
                .name("test")
                .phone("123456789")
                .password("password")
                .address("address")
                .build();

        SignUpResponseDTO responseDTO = SignUpResponseDTO.builder()
                .id(1L)
                .email("test@uzem.com")
                .status(PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        //when
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(authService.signUp(any())).thenReturn(responseDTO);

        //then
        mockMvc.perform(post("/auth/signup")
                        .with(csrf()) //CSRF 토큰 추가
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))//JSON 변환 후 요청
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@uzem.com"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("이메일 인증 성공")
    @WithMockUser(username = "testuser", roles = {"USER"}) //가짜 사용자 추가
    void successVerifyCheck() throws Exception {
        //given
        EmailVerificationRequestDTO requestDTO = EmailVerificationRequestDTO.builder()
                .email("test@uzem.com")
                .verificationCode("123456")
                .build();

        EmailVerificationResponseDTO responseDTO = EmailVerificationResponseDTO.builder()
                .email("test@uzem.com")
                .status(ACTIVE)
                .build();
        //when
        when(authService.verifyCheck("test@uzem.com", "123456"))
                .thenReturn(responseDTO);
        //then
        mockMvc.perform(post("/auth/verify-check")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@uzem.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(authService, times(1))
                .verifyCheck("test@uzem.com", "123456");
    }
}