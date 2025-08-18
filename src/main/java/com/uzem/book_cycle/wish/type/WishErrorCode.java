package com.uzem.book_cycle.wish.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum WishErrorCode implements ErrorCode {
    WISH_BOOK_NOT_FOUND("관심도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_WISH_ITEM("이미 관심도서에 추가 된 도서입니다.", HttpStatus.CONFLICT)
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
