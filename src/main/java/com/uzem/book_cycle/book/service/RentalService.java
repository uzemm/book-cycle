package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalHistoryRepository rentalHistoryRepository;

    public void createRentalHistory(RentalBook rentalBook, Member member, LocalDate now) {
        RentalHistory rentalHistory = RentalHistory.from(rentalBook, member, now);
        rentalHistoryRepository.save(rentalHistory);
    }
}
