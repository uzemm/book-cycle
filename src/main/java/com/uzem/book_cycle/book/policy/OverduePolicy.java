package com.uzem.book_cycle.book.policy;

import org.springframework.stereotype.Component;

@Component
public class OverduePolicy {

    public long calculateOverdue(long overdueDays) {

        if(overdueDays <= 7) return 0;

        if(overdueDays <= 30) {
            return (overdueDays - 7) * 500;
        }

        // 23일치만 부과 (8~30일 = 23일간)
        return 500 * 23;
    }
}
