package com.uzem.book_cycle.admin.dto.sales;

import com.uzem.book_cycle.admin.type.BookQuality;
import com.uzem.book_cycle.admin.type.SalesStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private SalesStatus salesStatus;
    @NotNull
    private BookQuality bookQuality;
}
