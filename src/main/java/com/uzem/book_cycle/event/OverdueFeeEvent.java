package com.uzem.book_cycle.event;


import com.uzem.book_cycle.book.entity.RentalHistory;


public record OverdueFeeEvent(RentalHistory rentalHistory, long overdueDays) {

}
