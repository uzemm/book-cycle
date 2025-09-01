package com.uzem.book_cycle.external.payment.service;

import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.external.payment.dto.CancelPaymentRequestDTO;
import com.uzem.book_cycle.external.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.external.payment.dto.PaymentResponseDTO;

public interface PaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO request);
    PaymentResponseDTO  processCancelPayment(CancelPaymentRequestDTO request);
    PaymentResponseDTO getPayment(String paymentKey);
    PaymentResponseDTO processOverduePayment(PaymentRequestDTO request);
    PaymentResponseDTO getOverduePayment(Order order);
}
