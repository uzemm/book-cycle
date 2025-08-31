package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.naver.type.BookErrorCode;
import lombok.*;

@Getter
public class BookException extends RuntimeException {
    private final BookErrorCode bookErrorCode;

    public BookException(BookErrorCode bookErrorCode) {
        super(bookErrorCode.getMessage());
        this.bookErrorCode = bookErrorCode;
    }
}
