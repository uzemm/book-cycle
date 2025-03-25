package com.uzem.book_cycle.admin.dto.rentals;

import com.uzem.book_cycle.admin.dto.UpdateBookRequestDTO;
import com.uzem.book_cycle.admin.type.RentalsStatus;
import com.uzem.book_cycle.book.dto.RentalsPreviewDTO;
import com.uzem.book_cycle.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
public class RentalsBook extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String pubdate;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private Long depositFee;

    @Column(nullable = false)
    private RentalsStatus rentalsStatus;

    private LocalDate paymentDeadline;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private boolean isPublic;

    public static RentalsBook from(RentalsRequestDTO request) {
        return RentalsBook.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .image(request.getImage())
                .pubdate(request.getPubdate())
                .link(request.getLink())
                .depositFee(request.getDepositFee())
                .rentalsStatus(RentalsStatus.AVAILABLE)
                .isDeleted(false)
                .isPublic(true)
                .build();
    }

    public void updateRentalsBook(UpdateRentalsRequestDTO update){
        updateCommonBookFields(update);
        this.depositFee = update.getDepositFee();
    }

    private void updateCommonBookFields(UpdateBookRequestDTO update) {
        this.title = update.getTitle();
        this.author = update.getAuthor();
        this.publisher = update.getPublisher();
        this.isbn = update.getIsbn();
        this.description = update.getDescription();
        this.image = update.getImage();
        this.pubdate = update.getPubdate();
        this.link = update.getLink();
    }

    public void updateIsPublic(){
        this.isPublic = true;
    }

    public void delete(){
        this.isDeleted = true;
    }

    public RentalsPreviewDTO toSalesPreviewDTO(){
        return RentalsPreviewDTO.builder()
                .title(this.title)
                .author(this.author)
                .image(this.image)
                .depositFee(this.depositFee)
                .status(this.rentalsStatus)
                .build();
    }
}
