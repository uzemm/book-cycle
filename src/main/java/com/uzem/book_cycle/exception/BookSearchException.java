package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.external.booksearch.type.BookSearchErrorCode;
import lombok.*;

@Getter
public class BookSearchException extends RuntimeException {
    private final BookSearchErrorCode bookErrorCode;

    public BookSearchException(BookSearchErrorCode bookErrorCode) {
        super(bookErrorCode.getMessage());
        this.bookErrorCode = bookErrorCode;
    }
}
