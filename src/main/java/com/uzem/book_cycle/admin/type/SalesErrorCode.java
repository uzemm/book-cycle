package com.uzem.book_cycle.admin.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum SalesErrorCode implements ErrorCode {
    SALES_BOOK_NOT_FOUND("판매 도서를 찾을 수 없습니다."),
    ALREADY_SOLD_OUT_SALE_BOOK("이 도서는 이미 판매 완료된 도서입니다.")
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
