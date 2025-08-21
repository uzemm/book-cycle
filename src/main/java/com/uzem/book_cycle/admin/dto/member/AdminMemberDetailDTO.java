package com.uzem.book_cycle.admin.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminMemberDetailDTO {

    private Long id;
    private String name;
    private String email;
    private int point;

    // 대여
    private int activeRentalCnt; // 현재 대여중인 권수
    private int totalRentalCnt; // 누적
    private int reservationCnt; // 예약

    //주문
    private int orderCnt;
    private int cancelOrderCnt;
}

