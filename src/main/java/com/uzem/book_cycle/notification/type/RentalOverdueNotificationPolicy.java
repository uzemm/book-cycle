package com.uzem.book_cycle.notification.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum RentalOverdueNotificationPolicy {

    D7(7, "반납일로부터 일주일이 지났습니다. 내일부터 연체료가 하루 500원씩 발생합니다."),
    D17(17, "연체가 길어지고 있어요! 최대 연체료에 도달하기 전에 꼭 반납해 주세요."),
    D30(30, "연체료가 하루 최대치에 도달했으며, 이후 추가 연체료는 없습니다. 반납 부탁드립니다.");

    private final int overdueDay;
    private final String message;

    public static Optional<RentalOverdueNotificationPolicy> fromDaysOverdue(int day) {
        return Arrays.stream(values())
                .filter(
                policy -> policy.getOverdueDay() == day)
                .findFirst();
    }

}
