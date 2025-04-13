package com.uzem.book_cycle.cart.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CartErrorCode implements ErrorCode {
    SOLD_OUT_BOOK_CART_ADD_FAILED("품절된 도서는 장바구니에 담을 수 없습니다."),
    RENTED_BOOK_CART_ADD_FAILED("대여중인 도서는 장바구니에 담을 수 없습니다."),
    CART_NOT_FOUND("장바구니를 찾을 수 없습니다.")
    ;

    private String description;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.description;
    }
}
