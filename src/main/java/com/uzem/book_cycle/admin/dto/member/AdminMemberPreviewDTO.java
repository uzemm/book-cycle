package com.uzem.book_cycle.admin.dto.member;

import com.uzem.book_cycle.member.type.MemberStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminMemberPreviewDTO {

    private Long id;
    private String name;
    private String email;
    private long point;
    private MemberStatus status;
    private int rentalCnt;
    private int orderCnt;
}

