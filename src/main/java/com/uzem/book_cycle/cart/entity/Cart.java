package com.uzem.book_cycle.cart.entity;

import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.type.ItemType;
import jakarta.persistence.*;
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
public class Cart extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private Long bookId;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    public static Cart from(Long bookId, ItemType itemType, Member member) {
        if(bookId == null ||  itemType == null || member == null) {
            throw new IllegalArgumentException
                    ("bookId and itemType and member cannot be null");
        }
        return Cart.builder()
                .member(member)
                .bookId(bookId)
                .itemType(itemType)
                .build();
    }
}
