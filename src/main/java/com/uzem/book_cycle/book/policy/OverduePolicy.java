package com.uzem.book_cycle.book.policy;

import com.uzem.book_cycle.book.entity.RentalHistory;
import org.springframework.stereotype.Component;

@Component
public class OverduePolicy {

    public long calculateOverdue(RentalHistory rentalHistory, long overdueDays) {
        return rentalHistory.getPrice() * overdueDays;
    }
}
