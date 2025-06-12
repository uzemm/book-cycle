package com.uzem.book_cycle.order.dto;

import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.type.OrderStatus;
import com.uzem.book_cycle.payment.dto.PaymentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelOrderDTO {

    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime cancelAt;
    private PaymentResponseDTO payment;

    public static CancelOrderDTO from(Order order, PaymentResponseDTO payment) {

        return CancelOrderDTO.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .cancelAt(order.getUpdatedAt())
                .payment(payment)
                .build();
    }
}
