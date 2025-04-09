package com.uzem.book_cycle.admin.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RentalErrorCode implements ErrorCode {
    RENTAL_BOOK_NOT_FOUND("대여 도서를 찾을 수 없습니다."),
    ALREADY_RENTED("이미 대여 중인 도서입니다."),
    OVERDUE_RENTAL_BOOK("연체 중인 도서는 대여할 수 없습니다."),
    PENDING_PAYMENT_RENTAL_BOOK("결제 대기 중인 도서는 대여할 수 없습니다."),
    ALREADY_RESERVED_THIS_BOOK("이미 예약된 도서입니다."),
    ALREADY_RESERVED_BY_OTHER("다른 예약자가 존재하는 도서입니다."),
    BOOK_NOT_RENTED("대여 상태가 아닌 도서는 예약이 불가능합니다."),
    RESERVATION_NOT_FOUND("예약 도서를 찾을 수 없습니다."),
    RENTAL_HISTORY_NOT_FOUND("대여 이력을 찾을 수 없습니다."),
    CANNOT_CANCEL_PENDING_PAYMENT("결제 대기 상태 예약은 취소할 수 없습니다. ")
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
