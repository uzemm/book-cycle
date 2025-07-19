package com.uzem.book_cycle.notification.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    RESERVATION_FIRST("예약하신 '%s' 도서 순번이 되었습니다. 24시간 내에 결제해 주세요.", null),

    RENTAL_OVERDUE("'%s'의 도서의 반납일이 지나 연체가 발생했습니다. 빠른 반납 부탁드립니다.",
            "'%s' 외 %d권의 도서의 반납일이 지나 연체가 발생했습니다. 빠른 반납 부탁드립니다."),

    RETURN_DUE("'%s'의 반납 예정일이 3일 남았습니다.",
            "'%s' 외 %d권의 반납 예정일이 3일 남았습니다."),

    ORDER_SHIPPED("주문한 도서가 발송되었습니다.", null),

    QUESTION_ANSWER("문의에 대한 답변이 도착하였습니다.", null),

    ADMIN_NOTICE("운영자로부터 새로운 공지사항이 등록되었습니다.", null);

    private final String singleMessage;
    private final String multipleMessage;

    public String format(String title, int other) {
        return (other == 0)
                ? String.format(singleMessage, title)
                : String.format(multipleMessage, title, other);

    }
}
