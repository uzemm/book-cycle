package com.uzem.book_cycle.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uzem.book_cycle.payment.type.PaymentStatus;
import com.uzem.book_cycle.payment.type.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


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

    @JsonProperty("orderId")
    private String tossOrderId;

}
