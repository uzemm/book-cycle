package com.uzem.book_cycle.book.service;

import java.time.LocalDate;

public interface OverdueService {
    void updateStatusOverdue(LocalDate today);
    void calculateOverdueFee(LocalDate today);
}
