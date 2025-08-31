package com.uzem.book_cycle.admin.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum SalesErrorCode implements ErrorCode {
    SALES_BOOK_NOT_FOUND("판매 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_SOLD_OUT_SALE_BOOK("이 도서는 이미 판매 완료된 도서입니다.", HttpStatus.CONFLICT)
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
