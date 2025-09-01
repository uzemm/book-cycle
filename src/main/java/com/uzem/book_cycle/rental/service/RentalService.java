package com.uzem.book_cycle.rental.service;

import com.uzem.book_cycle.rental.dto.*;
import com.uzem.book_cycle.rental.entity.RentalBook;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.external.payment.dto.PaymentRequestDTO;

import java.time.LocalDate;
import java.util.List;

public interface RentalService {
    void createRentalHistory(Long rentalBookId, Long memberId, Long orderId, LocalDate now);
    GroupReturnResponseDTO returnRental(Long orderId, Long memberId,
                                        PaymentRequestDTO payment);
    RentalResponseDTO cancelPendingPayment(RentalBook rentalBook, Long memberId);
    List<RentalHistoryResponseDTO> getMyRentals(Long memberId);
    List<OverdueListResponseDTO> getMyOverdue(Long memberId);
    List<RentalHistoryListResponseDTO> getMyRentalHistories(Long memberId);
    void restoreRentalBookStatus(RentalBook rentalBook);
    void restoreRentalHistory(Order order, RentalBook rentalBook);
}
