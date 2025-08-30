package com.uzem.book_cycle.admin.dto.sales;

import com.uzem.book_cycle.sales.entity.SalesBook;
import com.uzem.book_cycle.admin.type.BookQuality;
import com.uzem.book_cycle.admin.type.SalesStatus;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSalesResponseDTO {

    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String description;
    private String image;
    private String pubdate;
    private String link;

    private Long price;
    private SalesStatus salesStatus;
    private BookQuality bookQuality;
    private boolean isDeleted;
    private boolean isPublic;

    public static AdminSalesResponseDTO create(SalesBook salesBook) {
        return AdminSalesResponseDTO.builder()
                .id(salesBook.getId())
                .title(salesBook.getTitle())
                .author(salesBook.getAuthor())
                .publisher(salesBook.getPublisher())
                .isbn(salesBook.getIsbn())
                .description(salesBook.getDescription())
                .image(salesBook.getImage())
                .pubdate(salesBook.getPubdate())
                .link(salesBook.getLink())
                .price(salesBook.getPrice())
                .salesStatus(salesBook.getSalesStatus())
                .bookQuality(salesBook.getBookQuality())
                .isDeleted(salesBook.isDeleted())
                .isPublic(salesBook.isPublic())
                .build();
    }
}
