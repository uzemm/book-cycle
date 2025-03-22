package com.uzem.book_cycle.book.dto;

import com.uzem.book_cycle.admin.type.RentalsStatus;
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
}
