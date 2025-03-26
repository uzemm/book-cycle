package com.uzem.book_cycle.admin.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum RentalErrorCode {
    RENTAL_BOOK_NOT_FOUND("대여 도서를 찾을 수 없습니다.")
    ;

    private String description;
    private final List<String> fields;

    RentalErrorCode(String description, String... fields) {
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
