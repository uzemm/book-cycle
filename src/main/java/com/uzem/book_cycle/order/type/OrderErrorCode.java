package com.uzem.book_cycle.order.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND("주문내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_TOTAL_PRICE("총 결제 금액은 0원보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    ORDER_ITEM_NOT_FOUND("주문 항목이 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_ORDER("이미 처리된 주문입니다.", HttpStatus.CONFLICT),
    ORDER_STATUS_SHIPPED("배송 중인 도서는 취소할 수 없습니다.", HttpStatus.FORBIDDEN),
    ORDER_NOT_PAID("결제가 완료되지 않은 주문입니다.", HttpStatus.BAD_REQUEST),
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
