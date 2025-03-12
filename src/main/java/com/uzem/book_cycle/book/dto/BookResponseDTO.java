package com.uzem.book_cycle.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {

    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<BookDTO> items;

}
