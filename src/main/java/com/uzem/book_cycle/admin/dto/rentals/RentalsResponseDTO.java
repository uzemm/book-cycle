package com.uzem.book_cycle.admin.dto.rentals;

import com.uzem.book_cycle.admin.type.RentalsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalsResponseDTO {

    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String description;
    private String image;
    private String pubdate;
    private String link;

    private int depositFee;
    private RentalsStatus rentalsStatus;
    private boolean isDeleted;
    private boolean isPublic;

    public static RentalsResponseDTO create(RentalsBook rentalsBook) {
        return RentalsResponseDTO.builder()
                .id(rentalsBook.getId())
                .title(rentalsBook.getTitle())
                .author(rentalsBook.getAuthor())
                .publisher(rentalsBook.getPublisher())
                .isbn(rentalsBook.getIsbn())
                .description(rentalsBook.getDescription())
                .image(rentalsBook.getImage())
                .pubdate(rentalsBook.getPubdate())
                .link(rentalsBook.getLink())
                .rentalsStatus(rentalsBook.getRentalsStatus())
                .isDeleted(rentalsBook.isDeleted())
                .isPublic(rentalsBook.isPublic())
                .depositFee(rentalsBook.getDepositFee())
                .build();
    }
}
