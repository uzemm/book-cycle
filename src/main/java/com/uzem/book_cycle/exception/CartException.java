package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.cart.type.CartErrorCode;
import lombok.Getter;

@Getter
public class CartException extends RuntimeException {
    private final CartErrorCode cartErrorCode;

    public CartException(CartErrorCode cartErrorCode){
        super(cartErrorCode.getMessage());
        this.cartErrorCode = cartErrorCode;
    }
}
