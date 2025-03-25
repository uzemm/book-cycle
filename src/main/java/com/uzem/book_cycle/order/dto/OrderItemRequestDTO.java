package com.uzem.book_cycle.order.dto;

import com.uzem.book_cycle.order.type.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {

    private Long bookId;
    private ItemType itemType; // SALE or RENTAL
    private Long itemPrice;
}
