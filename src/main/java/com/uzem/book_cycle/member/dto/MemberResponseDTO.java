package com.uzem.book_cycle.member.dto;

import com.uzem.book_cycle.member.entity.Member;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Long point;
    private int rentalCnt;

    public static MemberResponseDTO from(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .address(member.getAddress())
                .rentalCnt(member.getRentalCnt())
                .point(member.getPoint())
                .build();
    }
}
