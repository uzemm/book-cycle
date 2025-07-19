package com.uzem.book_cycle.event;


import com.uzem.book_cycle.book.entity.RentalHistory;
import lombok.Getter;


@Getter
public class OverdueFeeEvent{

    private final RentalHistory rentalHistory;
    private final long overdueDays;

    public OverdueFeeEvent(RentalHistory rentalHistory, long overdueDays) {
        this.rentalHistory = rentalHistory;
        this.overdueDays = overdueDays;
    }
}
