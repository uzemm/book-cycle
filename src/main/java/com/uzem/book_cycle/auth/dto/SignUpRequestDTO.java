package com.uzem.book_cycle.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력하세요.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
        )
        private String password;

        @NotBlank(message = "이름을 입력하세요.")
        private String name;

        @NotBlank(message = "전화번호를 입력하세요.('-'제외)")
        @Size(min = 11, max = 11, message = "11자리 숫자만 입력하세요.")
        @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리로 입력해야 합니다.")
        private String phone;

        @NotBlank(message = "주소를 입력하세요.")
        private String address;

}

