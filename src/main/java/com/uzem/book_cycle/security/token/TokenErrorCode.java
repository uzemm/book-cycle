package com.uzem.book_cycle.security.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다."),
    NOT_A_REFRESH_TOKEN("리프레시 토큰이 아닙니다."),
    REFRESH_TOKEN_EXPIRED("리프레시 토큰이 만료되었습니다."),
    TOKEN_ALREADY_LOGGED_OUT("로그아웃된 토큰입니다."),
    UNSUPPORTED_TOKEN("지원되지 않는 JWT 토큰입니다."),
    ILLEGAL_ARGUMENT("JWT 토큰이 잘못되었습니다.")
    ;

    private String description;
}
