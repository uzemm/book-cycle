package com.uzem.book_cycle.admin.dto.rentals;

import com.uzem.book_cycle.admin.dto.sales.SalesBook;
import com.uzem.book_cycle.admin.type.RentalsStatus;
import com.uzem.book_cycle.admin.type.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalsPreviewDTO {
    private String title;
    private String author;
    private String image;
    private int depositFee;
    private RentalsStatus status;

    public static RentalsPreviewDTO fromEntity(RentalsBook rentalsBook) {
        return RentalsPreviewDTO.builder()
                .title(rentalsBook.getTitle())
                .author(rentalsBook.getAuthor())
                .image(rentalsBook.getImage())
                .depositFee(rentalsBook.getDepositFee())
                .status(rentalsBook.getRentalsStatus())
                .build();
    }
}
