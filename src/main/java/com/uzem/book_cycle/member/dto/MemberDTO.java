package com.uzem.book_cycle.member.dto;

import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.type.MemberStatus;
import com.uzem.book_cycle.member.type.Role;
import com.uzem.book_cycle.member.type.SocialType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

    private Long id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private MemberStatus status;
    private int rentalCnt;
    private Long point;
    private String refreshToken;
    private boolean isDeleted;
    private String socialId;
    private SocialType socialType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberDTO fromEntity(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .password(member.getPassword())
                .email(member.getEmail())
                .phone(member.getPhone())
                .address(member.getAddress())
                .role(member.getRole())
                .status(member.getStatus())
                .rentalCnt(member.getRentalCnt())
                .point(member.getPoint())
                .refreshToken(member.getRefreshToken())
                .isDeleted(false)
                .socialId(member.getSocialId())
                .socialType(member.getSocialType())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}


