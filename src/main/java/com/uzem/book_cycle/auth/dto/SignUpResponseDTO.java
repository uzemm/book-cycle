package com.uzem.book_cycle.auth.dto;

import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.type.MemberStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponseDTO {
    private Long id;
    private String email;
    private MemberStatus status;
    private LocalDateTime createdAt;

    public static SignUpResponseDTO from(Member member) {
        return SignUpResponseDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
