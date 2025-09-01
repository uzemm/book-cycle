package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.sales.type.SalesErrorCode;
import lombok.*;

@Getter
public class SalesException extends RuntimeException {

    private final SalesErrorCode salesErrorCode;

    public SalesException(SalesErrorCode salesErrorCode) {
        super(salesErrorCode.getMessage());
        this.salesErrorCode = salesErrorCode;
    }
}
