package com.uzem.book_cycle.wish.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum WishErrorCode implements ErrorCode {
    WISH_BOOK_NOT_FOUND("관심도서를 찾을 수 없습니다."),
    ALREADY_ADDED_TO_WISH("이미 관심도서에 추가 된 도서입니다.")
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
