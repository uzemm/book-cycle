package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.dto.ReservationResponseDTO;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.member.entity.Member;

import java.time.LocalDate;
import java.util.List;

public interface RentalService {
    void createRentalHistory(RentalBook rentalBook, Member member, LocalDate now);
    long calculateOverdueFee(RentalHistory rentalHistory, LocalDate now);
    ReservationResponseDTO createReservation(RentalBook rentalBook, Member member);
    void cancelMyReservation(RentalBook rentalBook, Member member);
    List<ReservationResponseDTO> getMyReservations(Member member);

}
