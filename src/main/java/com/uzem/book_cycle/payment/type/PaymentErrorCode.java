package com.uzem.book_cycle.payment.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_NOT_FOUND("결제내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TOSS_PAYMENT_REQUEST_FAILED("토스 결제 요청 실패", HttpStatus.BAD_GATEWAY),
    PAYMENT_SESSION_MISMATCH("세션의 결제 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST)
    ;


    private final String description;
    private final HttpStatus httpStatus;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.description;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
