package com.uzem.book_cycle.external.payment.dto;

import com.uzem.book_cycle.payment.entity.TossPayment;
import com.uzem.book_cycle.external.payment.type.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentDTO {
    private String paymentKey;
    private Long amount;
    private PaymentStatus status;
    private LocalDateTime approvedAt;

    public static PaymentDTO from(TossPayment payment) {
        return PaymentDTO.builder()
                .paymentKey(payment.getPaymentKey())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .approvedAt(LocalDateTime.now())
                .build();
    }
}
