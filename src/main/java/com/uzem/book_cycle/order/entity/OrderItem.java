package com.uzem.book_cycle.order.entity;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.order.type.ItemType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static com.uzem.book_cycle.order.type.ItemType.RENTAL;
import static com.uzem.book_cycle.order.type.ItemType.SALE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "sales_book_id")
    private SalesBook salesBook;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "rental_book_id")
    private RentalBook rentalBook;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType; // SALE or RENTAL

    @Column(nullable = false)
    private Long itemPrice;

    public void setOrder (Order order) {
        this.order = order;
    }

    public static OrderItem fromSales(Order order,
                SalesBook salesBook) {
        return OrderItem.builder()
                    .order(order)
                    .itemType(SALE)
                    .itemPrice(salesBook.getPrice())
                    .build();
    }
    public static OrderItem fromRental(Order order,
                                 RentalBook rentalBook) {
        return OrderItem.builder()
                .order(order)
                .itemType(RENTAL)
                .itemPrice(rentalBook.getPrice())
                .build();
    }
}
