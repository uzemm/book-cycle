package com.uzem.book_cycle.notification.type;

import lombok.Getter;

@Getter
public enum NotificationType {
    RESERVATION_FIRST("예약 순번이 되었습니다. 24시간 내에 결제해 주세요."),
    RENTAL_OVERDUE("대여 도서가 연체되었습니다."),
    RETURN_DUE("대여 도서 반납 예정일이 다가왔습니다."),
    ORDER_SHIPPED("주문한 도서가 발송되었습니다."),
    QUESTION_ANSWER("문의에 대한 답변이 도착하였습니다.");

    private final String defaultMessage;

    NotificationType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
