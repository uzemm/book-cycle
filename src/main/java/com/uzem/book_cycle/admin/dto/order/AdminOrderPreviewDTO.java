package com.uzem.book_cycle.admin.dto.order;

import com.uzem.book_cycle.order.type.OrderStatus;
import com.uzem.book_cycle.order.type.ShippingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminOrderPreviewDTO {
    private Long orderId;
    private String orderNumber;
    private String name;
    private String email;
    private OrderStatus orderStatus;
    private ShippingStatus shippingStatus;
    private LocalDateTime createdAt;
    private int totalPrice;
}
