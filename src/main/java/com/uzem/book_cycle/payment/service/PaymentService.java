package com.uzem.book_cycle.payment.service;

import com.uzem.book_cycle.payment.dto.CancelRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentResponseDTO;

public interface PaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO request);
    PaymentResponseDTO  processCancelPayment(CancelRequestDTO request);
    PaymentResponseDTO getPayment(String paymentKey);
    PaymentResponseDTO processOverduePayment(PaymentRequestDTO request);
}
