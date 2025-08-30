package com.uzem.book_cycle.auth.service;

import com.uzem.book_cycle.auth.dto.LoginRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpRequestDTO;
import com.uzem.book_cycle.auth.dto.SignUpResponseDTO;
import com.uzem.book_cycle.external.email.DTO.EmailResendResponseDTO;
import com.uzem.book_cycle.external.email.DTO.EmailVerificationResponseDTO;
import com.uzem.book_cycle.security.token.TokenDTO;

public interface AuthService {
    SignUpResponseDTO signUp(SignUpRequestDTO request);
    EmailVerificationResponseDTO verifyCheck(String email, String verificationCode);
    TokenDTO login(LoginRequestDTO loginRequestDTO);
    void logout(String accessToken);
    TokenDTO reissueAccessToken(String refreshToken);
    EmailResendResponseDTO verificationCodeResend(String email);
}
