package com.uzem.book_cycle.rental.dto;

import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.rental.entity.RentalHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalHistoryDTO {
    private LocalDate rentalDate;
    private LocalDate returnDate; // 반납 예정일
    private LocalDate actualReturnDate;  // 실제 반납일 (null 가능)
    private RentalStatus rentalStatus;

    public static RentalHistoryDTO from(RentalHistory rentalHistory) {
        if (rentalHistory == null) {
            return null;
        }

        return RentalHistoryDTO.builder()
                .rentalDate(rentalHistory.getRentalDate())
                .returnDate(rentalHistory.getReturnDate())
                .actualReturnDate(rentalHistory.getActualReturnDate())
                .rentalStatus(rentalHistory.getRentalStatus())
                .build();
    }

}
