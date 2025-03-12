package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.book.type.BookErrorCode;
import lombok.*;

@Getter
public class BookException extends RuntimeException {
    private BookErrorCode bookErrorCode;
    private String errorMessage;

    public BookException(BookErrorCode bookErrorCode) {
        this.bookErrorCode = bookErrorCode;
        this.errorMessage = bookErrorCode.getDescription();
    }
}
