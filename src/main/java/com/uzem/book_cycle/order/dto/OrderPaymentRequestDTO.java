package com.uzem.book_cycle.order.dto;

import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class OrderPaymentRequestDTO {
    @Valid
    private OrderRequestDTO order;

    @Valid
    private PaymentRequestDTO payment;
}
