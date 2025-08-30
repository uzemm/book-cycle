package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.rental.type.RentalErrorCode;
import lombok.*;

@Getter
public class RentalException extends RuntimeException {

    private final RentalErrorCode rentalErrorCode;

    public RentalException(RentalErrorCode rentalErrorCode) {
        super(rentalErrorCode.getMessage());
        this.rentalErrorCode = rentalErrorCode;
    }
}
