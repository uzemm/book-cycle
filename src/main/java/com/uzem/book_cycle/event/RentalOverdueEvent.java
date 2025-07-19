package com.uzem.book_cycle.event;


import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.member.entity.Member;
import lombok.Getter;

import java.util.List;

@Getter
public class RentalOverdueEvent {

    private final Member member;
    private final List<RentalHistory> rentalHistories;
    private final String message;

    public RentalOverdueEvent(Member member, List<RentalHistory> rentalHistories, String message) {
        this.member = member;
        this.rentalHistories = rentalHistories;
        this.message = message;
    }

}
