package com.uzem.book_cycle.payment.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode {
    PAYMENT_NOT_FOUND("결제내역을 찾을 수 없습니다."),
    TOSS_PAYMENT_REQUEST_FAILED("토스 결제 요청 실패")
    ;

    private String description;
}
