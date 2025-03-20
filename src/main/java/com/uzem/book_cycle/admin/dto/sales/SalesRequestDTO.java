package com.uzem.book_cycle.admin.dto.sales;

import com.uzem.book_cycle.admin.type.BookQuality;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.uzem.book_cycle.admin.type.SalesStatus.AVAILABLE;

@Getter
@AllArgsConstructor
@Builder
public class SalesRequestDTO {

    private String id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String description;
    private String image;
    private String pubdate;
    private String link;

    @NotNull
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;
    @NotNull
    private BookQuality bookQuality;

    public SalesBook toSalesBook() {
        return SalesBook.builder()
                .title(this.getTitle())
                .author(this.getAuthor())
                .publisher(this.getPublisher())
                .isbn(this.getIsbn())
                .image(this.getImage())
                .link(this.getLink())
                .price(this.getPrice())
                .description(this.getDescription())
                .salesStatus(AVAILABLE)
                .bookQuality(this.getBookQuality())
                .pubdate(this.getPubdate())
                .soldAt(null)
                .isDeleted(false)
                .isPublic(false)
                .build();
    }
}
