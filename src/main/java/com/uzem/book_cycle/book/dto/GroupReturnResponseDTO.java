package com.uzem.book_cycle.book.dto;

import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.payment.dto.PaymentResponseDTO;
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
    private PaymentResponseDTO payment;
    private List<RentalHistoryResponseDTO> rentalHistory;

    // 연체도서 반납
    public static GroupReturnResponseDTO fromHistoryResponse(List<RentalHistoryResponseDTO> historyResponse,
                                              PaymentResponseDTO payment) {
        if(historyResponse.isEmpty()){
            throw new RentalException(RENTAL_HISTORY_NOT_FOUND);
        }

        Long orderId = historyResponse.get(0).getOrderId();

        return GroupReturnResponseDTO.builder()
                .orderId(orderId)
                .totalOverdueFee(payment.getAmount())
                .payment(payment)
                .rentalHistory(historyResponse)
                .build();
    }

    // 정상반납
    public static GroupReturnResponseDTO from(List<RentalHistory> rentalHistories,
                                              PaymentResponseDTO payment) {
        if(rentalHistories.isEmpty()){
            throw new RentalException(RENTAL_HISTORY_NOT_FOUND);
        }

        Long orderId = rentalHistories.get(0).getOrder().getId();
        List<RentalHistoryResponseDTO> rentalHistory = rentalHistories.stream()
                .map(history -> RentalHistoryResponseDTO.from(history, payment))
                .toList();

        return GroupReturnResponseDTO.builder()
                .orderId(orderId)
                .totalOverdueFee(0L) // 연체료없음
                .payment(null)
                .rentalHistory(rentalHistory)
                .build();
    }
}
