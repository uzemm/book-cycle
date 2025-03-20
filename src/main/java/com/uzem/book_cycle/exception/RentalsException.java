package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.admin.type.RentalsErrorCode;
import com.uzem.book_cycle.admin.type.SalesErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalsException extends RuntimeException {

    private RentalsErrorCode rentalsErrorCode;
    private String errorMessage;

    public RentalsException(RentalsErrorCode rentalsErrorCode) {
        this.rentalsErrorCode = rentalsErrorCode;
        this.errorMessage = rentalsErrorCode.getDescription();
    }
}
