package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.dto.*;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface RentalService {
    void createRentalHistory(RentalBook rentalBook, Member member, Order order, LocalDate now);
    long calculateOverdueFee(RentalHistory rentalHistory, LocalDate now);
    ReservationResponseDTO createReservation(RentalBook rentalBook, Long memberId);
    void cancelMyReservation(RentalBook rentalBook, Long memberId);
    List<ReservationResponseDTO> getMyReservations(Long memberId);
    GroupReturnResponseDTO returnRental(Long orderId, Long memberId,
                                        PaymentRequestDTO payment);
    RentalResponseDTO cancelPendingPayment(RentalBook rentalBook, Long memberId);
    List<RentalHistoryResponseDTO> getMyRentals(Long memberId);
    List<OverdueListResponseDTO> getMyOverdue(Long memberId);
    List<RentalHistoryListResponseDTO> getMyRentalHistories(Long memberId);

}
