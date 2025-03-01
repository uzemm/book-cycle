package com.uzem.book_cycle.security.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {
    NOT_A_REFRESH_TOKEN("리프레시 토큰이 아닙니다."),
    TOKEN_ALREADY_LOGGED_OUT("로그아웃된 토큰입니다."),
    INVALID_TOKEN("잘못된 JWT 서명입니다."),
    EXPIRED_TOKEN("만료된 JWT 토큰입니다."),
    UNSUPPORTED_TOKEN("지원되지 않는 JWT 토큰입니다."),
    ILLEGAL_TOKEN("JWT 토큰이 없습니다.")
    ;

    private String description;
}
