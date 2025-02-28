package com.uzem.book_cycle.auth.controller;

import com.uzem.book_cycle.auth.dto.*;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.auth.service.AuthService;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationRequestDTO;
import com.uzem.book_cycle.security.token.TokenDTO;
import com.uzem.book_cycle.member.dto.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "권한 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public SignUpResponseDTO signUp(
            @Parameter(description = "회원가입 정보 입력")
            @RequestBody @Valid SignUpRequestDTO request) {
        //회원가입
        MemberDTO memberDTO = authService.signUp(request);

        return SignUpResponseDTO.from(memberDTO);
    }

    @PostMapping("/verify-check")
    @Operation(summary = "인증번호 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    }
    )
    public ResponseEntity<EmailVerificationResponseDTO> verifyEmail
            (@RequestBody @Valid EmailVerificationRequestDTO request) {
        EmailVerificationResponseDTO response = authService.verifyCheck(
                request.getEmail(), request.getVerificationCode());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody@Valid LoginRequestDTO request) {
        TokenDTO tokenDTO = authService.login(request);

        return LoginResponseDTO.create(tokenDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody @Valid LogoutRequestDTO request) {
        authService.logout(request.getAccessToken(), request.getEmail());

        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDTO> refreshAccessToken(@RequestBody RefreshRequestDTO request) {

        TokenDTO tokenDTO = authService.reissueAccessToken(request.getRefreshToken());

        return ResponseEntity.ok(tokenDTO);
    }
}
