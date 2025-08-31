package com.uzem.book_cycle.security.token;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum TokenErrorCode implements ErrorCode{
    NOT_A_REFRESH_TOKEN("리프레시 토큰이 아닙니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_ALREADY_LOGGED_OUT("로그아웃된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("잘못된 JWT 서명입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("지원되지 않는 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    ILLEGAL_TOKEN("JWT 토큰이 없습니다.", HttpStatus.UNAUTHORIZED)
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
