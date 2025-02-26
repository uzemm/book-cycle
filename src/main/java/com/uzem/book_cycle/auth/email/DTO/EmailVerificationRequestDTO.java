package com.uzem.book_cycle.auth.email.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EmailVerificationRequestDTO {

    @Email(message = "이메일을 정확하게 입력하세요.")
    @NotBlank(message = "이메일을 입력하세요.")
    private String email;

    @NotBlank(message = "인증번호를 입력하세요.")
    private String verificationCode;
}
