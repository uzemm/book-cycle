package com.uzem.book_cycle.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class MemberUpdatePasswordRequestDTO {

    @NotBlank(message = "현재 비밀번호를 입력해 주십시오.")
    private String currentPassword;

    @NotBlank
    @Size(min = 8,
            message = "비밀번호는 최소 8자 이상, 최대 20자 이하로 입력해야 합니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String confirmPassword;
}
