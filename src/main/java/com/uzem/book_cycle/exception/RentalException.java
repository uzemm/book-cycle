package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.admin.type.RentalErrorCode;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalException extends RuntimeException {

    private RentalErrorCode rentalErrorCode;
    private String errorMessage;

    public RentalException(RentalErrorCode rentalErrorCode) {
        this.rentalErrorCode = rentalErrorCode;
        this.errorMessage = rentalErrorCode.getMessage();
    }
}
