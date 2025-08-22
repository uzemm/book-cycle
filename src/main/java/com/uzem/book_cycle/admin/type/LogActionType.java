package com.uzem.book_cycle.admin.type;

public enum LogActionType {
    POINT_UPDATE,     // 포인트 지급/차감
    STATUS_CHANGE,    // 회원 상태 변경
    RESET_PASSWORD,    // 비밀번호 초기화
    FORCE_DELETE; // 강제 탈퇴
}
