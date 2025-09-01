package com.uzem.book_cycle.rental.service;

import com.uzem.book_cycle.rental.entity.RentalHistory;

import java.time.LocalDate;
import java.util.List;

public interface OverdueService {
    void processOverdue(List<RentalHistory> rentalList);
    void processOverdueFees(LocalDate today);
}
