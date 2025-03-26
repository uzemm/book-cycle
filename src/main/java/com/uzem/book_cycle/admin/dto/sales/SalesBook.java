package com.uzem.book_cycle.admin.dto.sales;

import com.uzem.book_cycle.admin.dto.UpdateBookRequestDTO;
import com.uzem.book_cycle.admin.type.BookQuality;
import com.uzem.book_cycle.admin.type.SalesStatus;
import com.uzem.book_cycle.book.dto.SalesPreviewDTO;
import com.uzem.book_cycle.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
public class SalesBook extends BaseEntity {

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
    private Long price;

    @Column(nullable = false)
    private SalesStatus salesStatus;

    @Column(nullable = false)
    private BookQuality bookQuality;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(updatable = false)
    private LocalDateTime soldAt;

    public static SalesBook from(SalesRequestDTO request) {
        return SalesBook.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .image(request.getImage())
                .pubdate(request.getPubdate())
                .link(request.getLink())
                .price(request.getPrice())
                .bookQuality(request.getBookQuality())
                .salesStatus(SalesStatus.AVAILABLE)
                .isDeleted(false)
                .isPublic(true)
                .build();
    }

    public void updateSalesBook(UpdateSalesRequestDTO update){
        updateCommonBookFields(update);
        this.price = update.getPrice();
        this.bookQuality = update.getBookQuality();
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

    public void delete(){
        this.isDeleted = true;
    }

    public SalesPreviewDTO toPreviewDTO() {
        return SalesPreviewDTO.builder()
                .title(this.title)
                .author(this.author)
                .image(this.image)
                .price(this.price)
                .status(this.salesStatus)
                .build();
    }

    public void setSalesStatus(SalesStatus salesStatus) {
        this.salesStatus = salesStatus;
    }
}
