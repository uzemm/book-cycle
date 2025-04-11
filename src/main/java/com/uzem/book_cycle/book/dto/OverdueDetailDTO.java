package com.uzem.book_cycle.book.dto;

import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.entity.RentalHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverdueDetailDTO {

    private Long orderId;
    private String title;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private int overdueDays;
    private Long overdueFee;
    private RentalStatus rentalStatus;

    public static OverdueDetailDTO from(RentalHistory history) {
        return OverdueDetailDTO.builder()
                .orderId(history.getOrder().getId())
                .title(history.getRentalBook().getTitle())
                .rentalDate(history.getRentalDate())
                .returnDate(history.getReturnDate())
                .overdueFee(history.getOverdueFee())
                .rentalStatus(history.getRentalStatus())
                .build();
    }

}
