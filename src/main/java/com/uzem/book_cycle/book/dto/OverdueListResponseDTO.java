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
public class OverdueListResponseDTO {

    private Long orderId;
    private Long totalOverdueFee;
    private int count;
    private List<OverdueDetailDTO> overdueDetailList;

    public static OverdueListResponseDTO from(List<OverdueDetailDTO> list) {
        long totalOverdueFee = list.stream()
                .mapToLong(OverdueDetailDTO::getOverdueFee)
                .sum();

        return OverdueListResponseDTO.builder()
                .orderId(list.get(0).getOrderId())
                .totalOverdueFee(totalOverdueFee)
                .count(list.size())
                .overdueDetailList(list)
                .build();
    }
}
