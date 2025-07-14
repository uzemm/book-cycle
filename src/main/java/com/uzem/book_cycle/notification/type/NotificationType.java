package com.uzem.book_cycle.notification.type;

import lombok.Getter;

@Getter
public enum NotificationType {
    RESERVATION_FIRST("예약하신 도서 순번이 되었습니다. 24시간 내에 결제해 주세요."),
    RENTAL_OVERDUE("도서 반납 예정일이 하루 지나 대여 도서가 연체되었습니다."),
    RETURN_DUE("대여 도서 반납 예정일이 다가왔습니다."),
    ORDER_SHIPPED("주문한 도서가 발송되었습니다."),
    QUESTION_ANSWER("문의에 대한 답변이 도착하였습니다."),
    ADMIN_NOTICE("운영자로부터 새로운 공지사항이 등록되었습니다.");

    private final String defaultMessage;

    NotificationType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
