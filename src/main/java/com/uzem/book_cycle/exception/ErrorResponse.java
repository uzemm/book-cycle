package com.uzem.book_cycle.exception;

import lombok.*;


@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
}
