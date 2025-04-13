package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.cart.type.CartErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartException extends RuntimeException {
    private CartErrorCode cartErrorCode;
    private String errorMessage;

    public CartException(CartErrorCode cartErrorCode){
        this.cartErrorCode = cartErrorCode;
        this.errorMessage = cartErrorCode.getMessage();
    }
}
