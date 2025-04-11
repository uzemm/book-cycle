package com.uzem.book_cycle.book.dto;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalResponseDTO {

    private Long rentalId;
    private Long rentalBookId;
    private RentalStatus rentalStatus;
    private String title;

    public static RentalResponseDTO from(RentalBook rentalBook) {
        return RentalResponseDTO.builder()
                .rentalId(rentalBook.getId())
                .rentalBookId(rentalBook.getId())
                .rentalStatus(rentalBook.getRentalStatus())
                .title(rentalBook.getTitle())
                .build();
    }
}
