package com.uzem.book_cycle.member.dto;

import com.uzem.book_cycle.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class MemberUpdateRequestDTO {

    @NotBlank
    private String phone;

    @NotBlank
    private String address;

    private LocalDateTime updatedAt;

    public static MemberUpdateRequestDTO from(Member member) {
        return MemberUpdateRequestDTO.builder()
                .phone(member.getPhone())
                .address(member.getAddress())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}


