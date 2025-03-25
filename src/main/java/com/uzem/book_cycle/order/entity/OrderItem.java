package com.uzem.book_cycle.order.entity;

import com.uzem.book_cycle.admin.dto.rentals.RentalsBook;
import com.uzem.book_cycle.admin.dto.sales.SalesBook;
import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.order.dto.OrderItemRequestDTO;
import com.uzem.book_cycle.order.type.ItemType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_book_id")
    private SalesBook salesBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rentals_book_id")
    private RentalsBook rentalsBook;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType; // SALE or RENTAL

    @Column(nullable = false)
    private Long itemPrice;

    public void setOrder (Order order) {
        this.order = order;
    }

    public static OrderItem from(OrderItemRequestDTO request, Order order,
                SalesBook salesBook, RentalsBook rentalsBook) {
        // 도서 가격 타입별
        Long itemPrice = (request.getItemType() == ItemType.SALE)
                ? salesBook.getPrice()
                : rentalsBook.getDepositFee();

        OrderItem.OrderItemBuilder builder = OrderItem.builder()
                    .order(order)
                    .itemType(request.getItemType())
                    .itemPrice(itemPrice);

            if(request.getItemType() == ItemType.SALE){
                builder.salesBook(salesBook);
            } else{
                builder.rentalsBook(rentalsBook);
            }
            return builder.build();
    }
}
