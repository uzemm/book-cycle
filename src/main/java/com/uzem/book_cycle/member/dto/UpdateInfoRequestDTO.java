package com.uzem.book_cycle.member.dto;

import com.uzem.book_cycle.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UpdateInfoRequestDTO {

    @NotBlank(message = "전화번호를 입력하세요.('-'제외)")
    @Size(min = 11, max = 11, message = "11자리 숫자만 입력하세요.")
    @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리로 입력해야 합니다.")
    private String phone;

    @NotBlank
    private String address;

    private LocalDateTime updatedAt;

    public static UpdateInfoRequestDTO from(Member member) {
        return UpdateInfoRequestDTO.builder()
                .phone(member.getPhone())
                .address(member.getAddress())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}


