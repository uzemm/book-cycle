package com.uzem.book_cycle.admin.dto.sales;

import com.uzem.book_cycle.admin.type.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesPreviewDTO {
    private String title;
    private String author;
    private String image;
    private int price;
    private SalesStatus status;

    public static SalesPreviewDTO fromEntity(SalesBook salesBook) {
        return SalesPreviewDTO.builder()
                .title(salesBook.getTitle())
                .author(salesBook.getAuthor())
                .image(salesBook.getImage())
                .price(salesBook.getPrice())
                .status(salesBook.getSalesStatus())
                .build();
    }
}
