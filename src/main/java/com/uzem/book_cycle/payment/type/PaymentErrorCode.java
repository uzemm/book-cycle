package com.uzem.book_cycle.payment.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_NOT_FOUND("결제내역을 찾을 수 없습니다."),
    TOSS_PAYMENT_REQUEST_FAILED("토스 결제 요청 실패"),
    PAYMENT_SESSION_MISMATCH("세션의 결제 정보가 일치하지 않습니다.")
    ;

    private final String description;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.description;
    }
}
