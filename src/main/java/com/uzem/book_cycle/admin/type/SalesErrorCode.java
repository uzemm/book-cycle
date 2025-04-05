package com.uzem.book_cycle.admin.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum SalesErrorCode {
    SALES_BOOK_NOT_FOUND("판매 도서를 찾을 수 없습니다."),
    ALREADY_SOLD_OUT_SALE_BOOK("이 도서는 이미 판매 완료된 도서입니다.")
    ;

    private String description;
    private final List<String> fields;

    SalesErrorCode(String description, String... fields) {
        this.description = description;
        this.fields = Arrays.asList(fields);
    }

    public Map<String, String> toErrorMap() {
        return fields.stream().collect(Collectors.toMap(field -> field, field -> description));
    }

    public String getDescription() {
        return description;
    }
}
