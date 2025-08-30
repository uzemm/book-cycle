package com.uzem.book_cycle.reservation.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
    RESERVATION_ALREADY_EXISTS("이미 예약한 도서입니다.", HttpStatus.CONFLICT),
    RESERVATION_FULL("예약 인원이 모두 찼습니다.", HttpStatus.CONFLICT),
    RESERVATION_ALLOWED_ONLY_WHEN_RENTED("대여 중인 도서만 예약할 수 있습니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_FOUND("예약 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PENDING_PAYMENT_RESERVATION_CANNOT_BE_CANCELED("결제 대기 상태 예약은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_RESERVATION_ORDER("예약 순번이 아니거나 유효하지 않은 예약입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String description;
    private final HttpStatus httpStatus;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.description;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
