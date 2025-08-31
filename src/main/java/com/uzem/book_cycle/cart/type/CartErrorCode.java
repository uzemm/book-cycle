package com.uzem.book_cycle.cart.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum CartErrorCode implements ErrorCode {
    SOLD_OUT_BOOK_CART_ADD_FAILED("품절된 도서는 장바구니에 담을 수 없습니다.", HttpStatus.BAD_REQUEST),
    RENTED_BOOK_CART_ADD_FAILED("대여중인 도서는 장바구니에 담을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND("선택한 장바구니 항목이 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_CART_ITEM("이미 장바구니에 담겨있는 도서입니다.", HttpStatus.CONFLICT),
    RESERVATION_NOT_OWNED("예약자 본인만 장바구니에 담을 수 있습니다.", HttpStatus.FORBIDDEN),
    NOT_PENDING_PAYMENT_BOOK("결제대기도서만 장바구니에 담을 수 있습니다.", HttpStatus.BAD_REQUEST)
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
