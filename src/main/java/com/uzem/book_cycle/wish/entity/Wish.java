package com.uzem.book_cycle.wish.entity;


import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
public class Wish extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_book_id", nullable = false)
    private SalesBook salesBook;

    public static Wish from(SalesBook salesBook, Member member) {
        if(salesBook == null || member == null) {
            throw new IllegalArgumentException("salesBook and member cannot be null");
        }

        return Wish.builder()
                .member(member)
                .salesBook(salesBook)
                .build();
    }

}
