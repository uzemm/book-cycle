package com.uzem.book_cycle.admin.dto.order;

import com.uzem.book_cycle.order.dto.OrderItemResponseDTO;
import com.uzem.book_cycle.order.type.OrderStatus;
import com.uzem.book_cycle.order.type.ShippingStatus;
import com.uzem.book_cycle.external.payment.dto.PaymentResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminOrderDetailDTO {
    private Long id;
    private String orderNumber;
    private String memberName;
    private String memberEmail;
    private String memberAddress;
    private String memberPhone;
    private OrderStatus orderStatus;
    private ShippingStatus shippingStatus;
    private LocalDateTime createdAt;
    private int totalPrice;
    private List<OrderItemResponseDTO> items;
    private PaymentResponseDTO payment;
}
