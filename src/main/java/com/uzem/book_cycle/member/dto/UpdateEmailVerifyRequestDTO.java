package com.uzem.book_cycle.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateEmailVerifyRequestDTO {

    @NotBlank
    private String newEmail;

    @NotBlank(message = "인증코드를 입력해야 합니다.")
    private String verificationCode;
}
