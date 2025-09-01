package com.uzem.book_cycle.external.booksearch.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum BookSearchErrorCode implements ErrorCode {
    BOOK_NOT_FOUND("해당 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EMPTY_SEARCH_QUERY("검색어를 입력하세요.", HttpStatus.BAD_REQUEST),
    NAVER_API_ERROR("네이버 API 호출 중 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY);

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
