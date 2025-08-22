package com.uzem.book_cycle.order.type;

public enum CancelReason {
    USER_REQUEST,   // 사용자가 직접 취소
    AUTO_EXPIRED,   // 결제 대기 초과
    PAYMENT_FAILED, // 결제 실패
    ADMIN_FORCE    // 관리자 강제 취소
}
