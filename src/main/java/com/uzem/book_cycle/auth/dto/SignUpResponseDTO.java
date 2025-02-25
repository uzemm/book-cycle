package com.uzem.book_cycle.auth.dto;

import com.uzem.book_cycle.member.dto.MemberDTO;
import com.uzem.book_cycle.member.type.MemberStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponseDTO {
    private Long id;
    private String email;
    private MemberStatus status;
    private LocalDateTime createdAt;

    public static SignUpResponseDTO from(MemberDTO memberDto) {
        return SignUpResponseDTO.builder()
                .id(memberDto.getId())
                .email(memberDto.getEmail())
                .status(memberDto.getStatus())
                .createdAt(memberDto.getCreatedAt())
                .build();
    }
}
