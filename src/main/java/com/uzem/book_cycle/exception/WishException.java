package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.wish.type.WishErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class WishException extends RuntimeException {
    private final WishErrorCode wishErrorCode;

    public WishException(WishErrorCode wishErrorCode){
        super(wishErrorCode.getMessage());
        this.wishErrorCode = wishErrorCode;
    }
}
