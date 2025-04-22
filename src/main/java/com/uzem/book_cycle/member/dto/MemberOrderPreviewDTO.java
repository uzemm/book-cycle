package com.uzem.book_cycle.member.dto;

import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberOrderPreviewDTO {

    private Long orderId;
    private String orderName;
    private Long totalPrice;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;

    public static MemberOrderPreviewDTO from(Order order) {
        return MemberOrderPreviewDTO.builder()
                .orderId(order.getId())
                .orderName(order.getOrderName())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
