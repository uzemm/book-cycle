package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.book.entity.RentalHistory;

import java.time.LocalDate;
import java.util.List;

public interface OverdueService {
    void processOverdue(List<RentalHistory> rentalList);
    void processOverdueFees(LocalDate today);
}
