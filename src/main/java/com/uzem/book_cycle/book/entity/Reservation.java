package com.uzem.book_cycle.book.entity;

import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

import static com.uzem.book_cycle.admin.type.RentalStatus.PENDING_PAYMENT;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Reservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_book_id", nullable = false)
    private RentalBook rentalBook;

    private LocalDate paymentDeadline;

    @Column(nullable = false)
    private int reservationOrder; // 예약 순번

    @Column(nullable = false)
    private boolean isActive; // 유효한 예약인지

    public static Reservation create(RentalBook rentalBook, Member member) {
        Reservation reservation = Reservation.builder()
                .member(member)
                .paymentDeadline(null)
                .isActive(true)
                .build();
        reservation.setRentalBook(rentalBook);
        return reservation;
    }

    public void deleteRentalBook() {
        if(rentalBook != null) {
            this.rentalBook = null;
        }
    }

    public boolean isPendingPayment() {
        return this.rentalBook.getRentalStatus() == PENDING_PAYMENT;
    }

    public void updatePaymentDeadline(LocalDate paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public void updateReservationOrder(int reservationOrder) {
        this.reservationOrder = reservationOrder;
    }

    public void cancelReservation() {
        this.isActive = false;
        this.reservationOrder = 0;
        this.paymentDeadline = null;
    }
}
