package com.uzem.book_cycle.book.entity;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @OneToOne
    @JoinColumn(name = "rental_id", nullable = false, unique = true)
    private RentalBook rentalBook;

    private LocalDate paymentDeadline;

    public static Reservation create(RentalBook rentalBook, Member member) {
        Reservation reservation = Reservation.builder()
                .member(member)
                .paymentDeadline(null)
                .build();
        reservation.setRentalBook(rentalBook);
        return reservation;
    }

    public void setRentalBook(RentalBook rentalBook) {
        this.rentalBook = rentalBook;
        rentalBook.setReservation(this);
    }

    public void deleteRentalBook() {
        if(rentalBook != null) {
            this.rentalBook.setReservation(null);
            this.rentalBook = null;
        }
    }

    public boolean isPendingPayment() {
        return this.rentalBook.getRentalStatus() == PENDING_PAYMENT;
    }

    public void updatePaymentDeadline(LocalDate paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }
}
