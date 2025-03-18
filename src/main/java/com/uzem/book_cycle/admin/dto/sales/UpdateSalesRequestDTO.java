package com.uzem.book_cycle.admin.dto.sales;

import com.uzem.book_cycle.admin.type.BookQuality;
import com.uzem.book_cycle.admin.type.SalesStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateSalesRequestDTO {

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
    private int price;
    @NotNull
    private SalesStatus salesStatus;
    @NotNull
    private BookQuality bookQuality;

}
