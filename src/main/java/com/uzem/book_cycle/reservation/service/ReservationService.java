package com.uzem.book_cycle.reservation.service;

import com.uzem.book_cycle.reservation.dto.ReservationResponseDTO;
import com.uzem.book_cycle.rental.entity.RentalBook;
import com.uzem.book_cycle.rental.entity.RentalHistory;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.reservation.entity.Reservation;

import java.util.List;

public interface ReservationService {
    ReservationResponseDTO createReservation(Long rentalBookId, Long memberId);
    void cancelMyReservation(RentalBook rentalBook, Long memberId);
    List<ReservationResponseDTO> getMyReservations(Long memberId);
    void updateCancelPendingPayment(List<Reservation> reservations);
    void updateReservationPaymentDeadline(RentalBook rentalBook);
    void notifyNextReservation(List<RentalHistory> rentalHistories);
    Reservation getReservation(RentalBook rentalBook, Long memberId);
    void cancelPendingReservation(Reservation reservation);
    void handleReservation(Long rentalId, Member member, boolean isPayment);
    void afterPaymentSuccess(Long rentalId, Member member);
    void afterCancelOrExpire(Long rentalId, Member member);
}
