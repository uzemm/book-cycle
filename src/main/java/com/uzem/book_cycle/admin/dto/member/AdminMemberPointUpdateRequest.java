package com.uzem.book_cycle.admin.dto.member;

import lombok.Getter;

@Getter
public class AdminMemberPointUpdateRequest {

    private Long amount;
    private String reason; // 수정 사유
}
