package com.uzem.book_cycle.payment.dto;

import com.uzem.book_cycle.payment.entity.Cancel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelResponseDTO {

    private String paymentKey;
    private String cancelReason;
    private Long cancelAmount;
    private OffsetDateTime canceledAt;
    private String transactionKey;
    private String cancelStatus;
    private Long refundableAmount;

    public static CancelResponseDTO from(Cancel cancel) {
        return CancelResponseDTO.builder()
                .paymentKey(cancel.getPaymentKey())
                .cancelReason(cancel.getCancelReason())
                .cancelAmount(cancel.getCancelAmount())
                .canceledAt(cancel.getCanceledAt())
                .transactionKey(cancel.getTransactionKey())
                .cancelStatus(cancel.getCancelStatus())
                .refundableAmount(cancel.getRefundableAmount())
                .build();
    }
}
