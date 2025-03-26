package com.uzem.book_cycle.book.dto;

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
    private Long price;
    private SalesStatus status;
}
