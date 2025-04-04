package com.uzem.book_cycle.book.entity;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

import static com.uzem.book_cycle.admin.type.RentalStatus.OVERDUE;
import static com.uzem.book_cycle.admin.type.RentalStatus.RENTED;

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
    private boolean isOverduePayment; // // 최종 결제 여부 (14일 초과 시 책 정가 결제)

    public static RentalHistory from(RentalBook rentalBook, Member member, LocalDate now) {
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
                .build();
    }

    public void statusOverdue(){
        this.rentalStatus = OVERDUE;
    }
}
