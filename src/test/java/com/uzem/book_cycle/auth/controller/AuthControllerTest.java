package com.uzem.book_cycle.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzem.book_cycle.auth.dto.LoginRequestDTO;
import com.uzem.book_cycle.auth.dto.RefreshRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpResponseDTO;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.auth.service.AuthService;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationRequestDTO;
import com.uzem.book_cycle.auth.email.service.EmailService;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.security.CustomUserDetailsService;
import com.uzem.book_cycle.security.token.TokenDTO;
import com.uzem.book_cycle.security.token.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    TokenProvider tokenProvider;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

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

    @Test
    @DisplayName("로그인 성공 - Security 필터 통과")
    @WithMockUser(username = "test@uzem.com", roles = {"USER"})
    void successLogin() throws Exception {
        //given
        TokenDTO tokenDTO = TokenDTO.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        LoginRequestDTO request = new LoginRequestDTO("test@uzem.com", "password123");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(tokenDTO);

        //when
        //then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        verify(authService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("로그아웃 성공")
    @WithMockUser(username = "test@uzem.com", roles = {"USER"})
    void successLogout() throws Exception {
        //given
        String accessToken = "accessToken";
        String email = "test@uzem.com";

        when(tokenProvider.resolveToken(any(HttpServletRequest.class)))
                .thenReturn(accessToken);
        when(tokenProvider.getAuthentication(accessToken)).thenReturn(
                new UsernamePasswordAuthenticationToken(email, ""));
        //when
        //then
        mockMvc.perform(post("/auth/logout")
                        .with(csrf())
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("로그아웃 성공"));

        verify(authService).logout(eq(accessToken));
    }

    @Test
    @DisplayName("AccessToken 재발급 성공")
    void success_reissueAccessToken() throws Exception {
        //given
        String refreshToken = "refreshToken";
        RefreshRequestDTO request = RefreshRequestDTO.builder()
                .refreshToken(refreshToken).build();

        TokenDTO tokenDTO = TokenDTO.builder()
                .accessToken("new_accessToken")
                .refreshToken("refreshToken")
                .build();

        when(tokenProvider.reissueAccessToken(refreshToken)).thenReturn(tokenDTO);

        //when
        //then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new_accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

}