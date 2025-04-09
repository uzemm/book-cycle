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
public class RentalHistoryResponseDTO {

    private Long rentalId;
    private Long rentalBookId;
    private Long memberId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private RentalStatus rentalStatus;

    public static RentalHistoryResponseDTO from(RentalHistory rentalHistory) {
        return RentalHistoryResponseDTO.builder()
                .rentalId(rentalHistory.getId())
                .rentalBookId(rentalHistory.getRentalBook().getId())
                .memberId(rentalHistory.getMember().getId())
                .rentalDate(rentalHistory.getRentalDate())
                .returnDate(rentalHistory.getReturnDate())
                .actualReturnDate(rentalHistory.getActualReturnDate())
                .rentalStatus(rentalHistory.getRentalStatus())
                .build();
    }
}
