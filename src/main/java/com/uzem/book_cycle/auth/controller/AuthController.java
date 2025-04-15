package com.uzem.book_cycle.auth.controller;

import com.uzem.book_cycle.auth.dto.*;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.auth.service.AuthService;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationRequestDTO;
import com.uzem.book_cycle.security.token.TokenDTO;
import com.uzem.book_cycle.security.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "권한 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenProvider tokenProvider;

    @Operation(summary = "회원가입", description = "Book-Cycle에 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(
            @RequestBody @Valid SignUpRequestDTO request) {
        //회원가입
        SignUpResponseDTO response = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "이메일 인증", description = "이메일 인증번호를 확인합니다.")
    @PostMapping("/verify-check")
    public ResponseEntity<EmailVerificationResponseDTO> verifyEmail
            (@RequestBody @Valid EmailVerificationRequestDTO request) {
        EmailVerificationResponseDTO response = authService.verifyCheck(
                request.getEmail(), request.getVerificationCode());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody@Valid LoginRequestDTO request) {
        TokenDTO tokenDTO = authService.login(request);

        return LoginResponseDTO.create(tokenDTO);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = tokenProvider.resolveToken(request);

        authService.logout(accessToken);

        return ResponseEntity.ok("로그아웃 성공");
    }

    @Operation(summary = "액세스토큰 재발급 요청")
    @PostMapping("/refresh")
    public ResponseEntity<TokenDTO> reissueAccessToken(@RequestBody @Valid RefreshRequestDTO request) {

        TokenDTO tokenDTO = authService.reissueAccessToken(request.getRefreshToken());

        return ResponseEntity.ok(tokenDTO);
    }
}
