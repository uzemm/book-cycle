package com.uzem.book_cycle.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalHistoryListResponseDTO {

    private Long orderId;
    private Long totalOverdueFee;
    private int count;
    private List<RentalHistoryResponseDTO> rentalHistoryList;

    public static RentalHistoryListResponseDTO from(List<RentalHistoryResponseDTO> list) {

        return RentalHistoryListResponseDTO.builder()
                .orderId(list.get(0).getOrderId())
                .totalOverdueFee(list.get(0).getPayment().getAmount())
                .count(list.size())
                .rentalHistoryList(list)
                .build();
    }
}
