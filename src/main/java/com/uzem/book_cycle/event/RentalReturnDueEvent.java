package com.uzem.book_cycle.event;


import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.member.entity.Member;

import java.util.List;

public record RentalReturnDueEvent(Member member,
                                   List<RentalHistory> rentalHistories,
                                   String message) {

}
