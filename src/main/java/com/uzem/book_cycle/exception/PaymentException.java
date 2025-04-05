package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.payment.type.PaymentErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentException extends RuntimeException {
    private PaymentErrorCode paymentErrorCode;
    private String errorMessage;

    public PaymentException(PaymentErrorCode paymentErrorCode){
        this.paymentErrorCode = paymentErrorCode;
        this.errorMessage = paymentErrorCode.getMessage();
    }
}
