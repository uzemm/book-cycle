package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.security.token.TokenErrorCode;
import lombok.*;

@Getter
public class TokenException extends RuntimeException {
    private final TokenErrorCode tokenErrorCode;

    public TokenException(TokenErrorCode tokenErrorCode) {
        super(tokenErrorCode.getMessage());
      this.tokenErrorCode = tokenErrorCode;
    }
}
