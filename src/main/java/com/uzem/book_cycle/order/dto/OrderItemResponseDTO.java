package com.uzem.book_cycle.order.dto;

import com.uzem.book_cycle.rental.dto.RentalHistoryDTO;
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
    private String title;

    // 대여 도서일 경우 RentalHistory 정보 포함
    private RentalHistoryDTO rentalHistory;

    public static OrderItemResponseDTO from(OrderItem item) {
        if(item.getItemType() == ItemType.SALE) {
            return OrderItemResponseDTO.builder()
                    .bookId(item.getSalesBook().getId())
                    .itemType(item.getItemType())
                    .itemPrice(item.getItemPrice())
                    .title(item.getSalesBook().getTitle())
                    .build();
        } else {
            return OrderItemResponseDTO.builder()
                    .bookId(item.getRentalBook().getId())
                    .itemType(item.getItemType())
                    .itemPrice(item.getItemPrice())
                    .title(item.getRentalBook().getTitle())
                    .rentalHistory(RentalHistoryDTO.from(item.getRentalHistory()))
                    .build();
        }
    }

}
