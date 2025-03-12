package com.uzem.book_cycle.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateEmailRequestDTO {

    private String currentEmail;

    @Email(message = "이메일을 정확하게 입력하세요.")
    @NotBlank(message = "이메일을 입력하세요.")
    private String newEmail;
}
