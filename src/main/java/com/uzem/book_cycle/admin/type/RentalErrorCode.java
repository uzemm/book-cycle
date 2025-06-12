package com.uzem.book_cycle.admin.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RentalErrorCode implements ErrorCode {
    RENTAL_BOOK_NOT_FOUND("대여 도서를 찾을 수 없습니다."),
    ALREADY_RENTED("이미 대여 중인 도서입니다."),
    OVERDUE_RENTAL_BOOK("연체 중인 도서는 대여할 수 없습니다."),
    PENDING_PAYMENT_RENTAL_BOOK("결제 대기 중인 도서는 대여할 수 없습니다."),
    ALREADY_RESERVED_BY_SELF("이미 예약된 도서입니다."),
    ALREADY_RESERVED_BY_OTHER("다른 예약자가 존재하는 도서입니다."),
    CANNOT_RESERVE_NON_RENTED_BOOK("대여 중인 도서만 예약할 수 있습니다."),
    RESERVATION_NOT_FOUND("예약 도서를 찾을 수 없습니다."),
    RENTAL_HISTORY_NOT_FOUND("대여 이력을 찾을 수 없습니다."),
    PENDING_PAYMENT_RESERVATION_CANNOT_BE_CANCELED("결제 대기 상태 예약은 취소할 수 없습니다. "),
    RENTAL_HISTORY_STATUS_MISMATCH("묶음 반납 도서의 대여상태가 일치하지 않습니다."),
    INVALID_RENTAL_STATUS("대여상태가 일치하지 않습니다.")
    ;

    private String description;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.description;
    }
}
