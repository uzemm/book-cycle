package com.uzem.book_cycle.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class SignUpRequestDTO {

        @Email(message = "이메일을 정확하게 입력하세요.")
        @NotBlank(message = "이메일을 입력하세요.")
        private String email;

        @NotBlank(message = "비밀번호를 입력하세요.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String password;

        @NotBlank(message = "이름을 입력하세요.")
        private String name;

        @NotBlank(message = "전화번호를 입력하세요.('-'제외)")
        private String phone;

        @NotBlank(message = "주소를 입력하세요.")
        private String address;

}

