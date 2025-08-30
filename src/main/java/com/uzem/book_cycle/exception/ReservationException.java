package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.reservation.type.ReservationErrorCode;
import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {

    private final ReservationErrorCode reservationErrorCode;

    public ReservationException(ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode.getMessage());
        this.reservationErrorCode = reservationErrorCode;
    }
}
