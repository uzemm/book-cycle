package com.uzem.book_cycle.auth.controller;

import com.uzem.book_cycle.auth.dto.SignUpRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpResponseDTO;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.auth.service.AuthService;
import com.uzem.book_cycle.auth.email.DTO.EmailVerificationRequestDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(
            @RequestBody @Valid SignUpRequestDTO request) {
        //회원가입
        SignUpResponseDTO response = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-check")
    public ResponseEntity<EmailVerificationResponseDTO> verifyEmail
            (@RequestBody @Valid EmailVerificationRequestDTO request) {
        EmailVerificationResponseDTO response = authService.verifyCheck(
                request.getEmail(), request.getVerificationCode());

        return ResponseEntity.ok(response);
    }

}
