package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.security.token.TokenErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenException extends RuntimeException {
    private TokenErrorCode tokenErrorCode;
    private String errorMessage;

    public TokenException(TokenErrorCode tokenErrorCode) {
      this.tokenErrorCode = tokenErrorCode;
      this.errorMessage = tokenErrorCode.getMessage();
    }
}
