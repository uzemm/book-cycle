package com.uzem.book_cycle.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uzem.book_cycle.payment.type.PaymentStatus;
import com.uzem.book_cycle.payment.type.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {

    private String paymentKey;
    private PaymentType type;
    private Long amount;
    private PaymentStatus status;
    private String orderName;
    private String cancelReason;
    private Long cancelAmount;
    private OffsetDateTime canceledAt;
    private String transactionKey;
    private String cancelStatus;
    private Long refundableAmount;

    @JsonProperty("orderId")
    private String tossOrderId;

}
