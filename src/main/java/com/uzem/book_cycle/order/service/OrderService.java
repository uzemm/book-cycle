package com.uzem.book_cycle.order.service;

import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.dto.OrderResponseDTO;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;

import java.time.LocalDate;

public interface OrderService {
    OrderResponseDTO confirmOrder(OrderRequestDTO orderRequestDTO,
                                  PaymentRequestDTO payment,
                                  Member member, LocalDate now);
    OrderResponseDTO getOrders(Long orderId);
}
