package com.uzem.book_cycle.naver.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookErrorCode {
    BOOK_NOT_FOUND("해당 도서를 찾을 수 없습니다."),
    EMPTY_SEARCH_QUERY("검색어를 입력하세요."),
    NAVER_API_ERROR("네이버 API 호출 중 오류가 발생했습니다.");


    private String description;
}
