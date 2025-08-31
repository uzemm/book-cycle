package com.uzem.book_cycle.order.service;

import com.uzem.book_cycle.order.dto.CancelOrderDTO;
import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.dto.OrderResponseDTO;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.type.CancelReason;
import com.uzem.book_cycle.external.payment.dto.CancelPaymentRequestDTO;
import com.uzem.book_cycle.external.payment.dto.PaymentRequestDTO;

import java.time.LocalDate;

public interface OrderService {
    OrderResponseDTO confirmOrder(OrderRequestDTO orderRequestDTO,
                                  PaymentRequestDTO payment,
                                  Long memberId, LocalDate now);
    OrderResponseDTO getOrderDetail(Long orderId);
    CancelOrderDTO cancelMyOrder(Long memberId, Long orderId, CancelPaymentRequestDTO requestDTO);
    void markOrderCancelPendingInNewTx(Order order);
    void cancelOrderWithRestorationInNewTx(Order order, CancelReason reason);
}
