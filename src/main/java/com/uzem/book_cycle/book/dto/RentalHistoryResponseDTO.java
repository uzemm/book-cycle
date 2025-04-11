package com.uzem.book_cycle.book.dto;

import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.payment.dto.PaymentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalHistoryResponseDTO {

    private Long rentalBookId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private RentalStatus rentalStatus;
    private Long orderId;
    private PaymentResponseDTO payment;

    // 연체료 결제 시
    public static RentalHistoryResponseDTO from(RentalHistory rentalHistory, PaymentResponseDTO payment) {
        return RentalHistoryResponseDTO.builder()
                .rentalBookId(rentalHistory.getRentalBook().getId())
                .rentalDate(rentalHistory.getRentalDate())
                .returnDate(rentalHistory.getReturnDate())
                .actualReturnDate(rentalHistory.getActualReturnDate())
                .rentalStatus(rentalHistory.getRentalStatus())
                .orderId(rentalHistory.getOrder().getId())
                .payment(payment)
                .build();
    }

    // 결제 x
    public static RentalHistoryResponseDTO from(RentalHistory rentalHistory) {
        return RentalHistoryResponseDTO.builder()
                .rentalBookId(rentalHistory.getRentalBook().getId())
                .rentalDate(rentalHistory.getRentalDate())
                .returnDate(rentalHistory.getReturnDate())
                .actualReturnDate(rentalHistory.getActualReturnDate())
                .rentalStatus(rentalHistory.getRentalStatus())
                .orderId(rentalHistory.getOrder().getId())
                .build();
    }
}
