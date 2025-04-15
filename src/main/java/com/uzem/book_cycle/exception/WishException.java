package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.wish.type.WishErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishException extends RuntimeException {
    private WishErrorCode wishErrorCode;
    private String errorMessage;

    public WishException(WishErrorCode wishErrorCode){
        this.wishErrorCode = wishErrorCode;
        this.errorMessage = wishErrorCode.getMessage();
    }
}
