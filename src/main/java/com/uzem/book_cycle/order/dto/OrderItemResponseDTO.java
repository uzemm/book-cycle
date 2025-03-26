package com.uzem.book_cycle.order.dto;

import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.entity.OrderItem;
import com.uzem.book_cycle.order.type.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {

    private Long bookId;
    private ItemType itemType; // SALE or RENTAL
    private Long itemPrice;

    public static OrderItemResponseDTO from(OrderItem item) {
        return OrderItemResponseDTO.builder()
                .bookId(item.getId())
                .itemType(item.getItemType())
                .itemPrice(item.getItemPrice())
                .build();
    }

}
