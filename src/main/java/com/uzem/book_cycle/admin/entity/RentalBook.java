package com.uzem.book_cycle.admin.entity;

import com.uzem.book_cycle.admin.dto.UpdateBookRequestDTO;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalRequestDTO;
import com.uzem.book_cycle.admin.dto.rental.UpdateAdminRentalRequestDTO;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.dto.RentalPreviewDTO;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;


import static com.uzem.book_cycle.admin.type.RentalStatus.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
public class RentalBook extends BaseEntity {
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
    private RentalStatus rentalStatus;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private boolean isPublic;

    @OneToOne(mappedBy = "rentalBook", cascade = CascadeType.ALL) // 양방향
    private Reservation reservation;

    public static RentalBook from(AdminRentalRequestDTO request) {
        return RentalBook.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .image(request.getImage())
                .pubdate(request.getPubdate())
                .link(request.getLink())
                .price(500L)
                .rentalStatus(AVAILABLE)
                .isDeleted(false)
                .isPublic(true)
                .build();
    }

    // 대여도서 수정
    public void updateRentalBook(UpdateAdminRentalRequestDTO update){
        updateCommonBookFields(update);
        this.price = update.getPrice();
        this.rentalStatus = update.getRentalStatus();
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

    // 공개
    public void updateIsPublic(){
        this.isPublic = true;
    }

    // 소프트 딜리트
    public void delete(){
        this.isDeleted = true;
    }

    public RentalPreviewDTO toRentalPreviewDTO(){
        return RentalPreviewDTO.builder()
                .title(this.title)
                .author(this.author)
                .image(this.image)
                .price(this.price)
                .status(this.rentalStatus)
                .build();
    }

    public RentalStatus rentalStatusRented() {
       return this.rentalStatus = RENTED;
    }

    // 대여 상태인지
    public boolean isRented() {
        return this.rentalStatus == RENTED;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void updateAvailable(){
        this.rentalStatus = AVAILABLE;
    }

    // 예약 차례 결제 대기
    public void updatePendingPayment(){
        this.rentalStatus = PENDING_PAYMENT;
    }

    public boolean isReservation(Reservation reservation) {
        return this.reservation == reservation;
    }
}
