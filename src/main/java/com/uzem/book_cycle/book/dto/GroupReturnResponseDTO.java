package com.uzem.book_cycle.book.dto;

import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalErrorCode.RENTAL_HISTORY_NOT_FOUND;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupReturnResponseDTO {
    private Long orderId;
    private Long totalOverdueFee;
    private PaymentRequestDTO payment;
    private List<RentalHistoryResponseDTO> rentalHistory;

    public static GroupReturnResponseDTO from(List<RentalHistory> rentalHistories, PaymentRequestDTO payment) {
        if(rentalHistories.isEmpty()){
            throw new RentalException(RENTAL_HISTORY_NOT_FOUND);
        }

        List<RentalHistoryResponseDTO> rentalHistory = rentalHistories.stream()
                .map(RentalHistoryResponseDTO::from)
                .toList();
        Long orderId = rentalHistories.get(0).getOrder().getId();

        return GroupReturnResponseDTO.builder()
                .orderId(orderId)
                .totalOverdueFee(payment.getAmount())
                .payment(payment)
                .rentalHistory(rentalHistory)
                .build();
    }
}
