package com.uzem.book_cycle.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.uzem.book_cycle.payment.entity.TossPayment;
import com.uzem.book_cycle.payment.type.PaymentStatus;
import com.uzem.book_cycle.payment.type.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private String paymentKey;
    private String orderId;
    private Long amount;
    private PaymentStatus status;
    private String orderName;
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;
    private PaymentType type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CancelResponseDTO> cancels;


    public static PaymentResponseDTO from(TossPayment tossPayment) {
        List<CancelResponseDTO> cancels = null;
        if(tossPayment.getStatus() == PaymentStatus.CANCELED) {
            cancels = tossPayment.getCancels().stream()
                    .map(CancelResponseDTO::from)
                    .collect(Collectors.toList());
        }

        return PaymentResponseDTO.builder()
                .paymentKey(tossPayment.getPaymentKey())
                .orderId(tossPayment.getTossOrderId())
                .amount(tossPayment.getAmount())
                .status(tossPayment.getStatus())
                .orderName(tossPayment.getOrderName())
                .type(tossPayment.getType())
                .requestedAt(tossPayment.getRequestedAt())
                .approvedAt(tossPayment.getApprovedAt())
                .cancels(cancels)
                .build();
    }

}
