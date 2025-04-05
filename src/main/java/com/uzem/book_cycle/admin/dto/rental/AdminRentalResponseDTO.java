package com.uzem.book_cycle.admin.dto.rental;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRentalResponseDTO {

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
    private RentalStatus rentalStatus;
    private boolean isDeleted;
    private boolean isPublic;

    public static AdminRentalResponseDTO create(RentalBook rentalBook) {
        return AdminRentalResponseDTO.builder()
                .id(rentalBook.getId())
                .title(rentalBook.getTitle())
                .author(rentalBook.getAuthor())
                .publisher(rentalBook.getPublisher())
                .isbn(rentalBook.getIsbn())
                .description(rentalBook.getDescription())
                .image(rentalBook.getImage())
                .pubdate(rentalBook.getPubdate())
                .link(rentalBook.getLink())
                .rentalStatus(rentalBook.getRentalStatus())
                .isDeleted(rentalBook.isDeleted())
                .isPublic(rentalBook.isPublic())
                .price(rentalBook.getPrice())
                .build();
    }
}
