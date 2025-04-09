package com.uzem.book_cycle.book.entity;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

import static com.uzem.book_cycle.admin.type.RentalStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Entity
public class RentalHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_book_id")
    private RentalBook rentalBook;

    @Column(nullable = false)
    private LocalDate rentalDate;

    @Column(nullable = false)
    private LocalDate returnDate;
    private LocalDate actualReturnDate;

    @Column(nullable = false)
    private Long price;
    private Long overdueFee;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RentalStatus rentalStatus;

    @Column(nullable = false)
    private boolean isOverduePayment; // 연체료 결제 연부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_Id")
    private Order order;

    public static RentalHistory from(RentalBook rentalBook, Member member,
                                     Order order, LocalDate now) {
        return RentalHistory.builder()
                .member(member)
                .rentalBook(rentalBook)
                .rentalDate(now)
                .returnDate(now.plusDays(14))
                .actualReturnDate(null)
                .price(rentalBook.getPrice())
                .overdueFee(0L)
                .rentalStatus(RENTED)
                .isOverduePayment(false)
                .order(order)
                .build();
    }

    public void statusOverdue(){ // rented -> overdue 변경
        this.rentalStatus = OVERDUE;
    }

    public void setOverdueFee(Long overdueFee) {
        this.overdueFee = overdueFee;
    }

    public void updateReturned(LocalDate actualReturnDate) { // 반납처리
        this.actualReturnDate = actualReturnDate;
        this.isOverduePayment = false;
        this.rentalStatus = RETURNED;
    }

    public void updateOverdueReturned(){ // 연체반납처리
        this.actualReturnDate = LocalDate.now();
        this.isOverduePayment = true;
        this.rentalStatus = RETURNED;
    }

    public void setMember(Member member) {
        this.member = member;
    }

}
