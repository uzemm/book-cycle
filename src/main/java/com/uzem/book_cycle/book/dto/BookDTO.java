package com.uzem.book_cycle.book.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDTO {

    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String description;
    private String image;
    private String pubdate;
    private String link;

}
