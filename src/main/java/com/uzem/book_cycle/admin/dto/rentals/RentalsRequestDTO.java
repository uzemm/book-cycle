package com.uzem.book_cycle.admin.dto.rentals;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.uzem.book_cycle.admin.type.RentalsStatus.AVAILABLE;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalsRequestDTO {

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
    private int depositFee;
}
