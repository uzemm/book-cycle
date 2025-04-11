package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.admin.type.SalesErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesException extends RuntimeException {

    private SalesErrorCode salesErrorCode;
    private String errorMessage;

    public SalesException(SalesErrorCode salesErrorCode) {
        this.salesErrorCode = salesErrorCode;
        this.errorMessage = salesErrorCode.getMessage();
    }
}
