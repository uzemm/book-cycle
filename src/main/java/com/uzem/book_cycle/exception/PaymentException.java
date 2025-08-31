package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.payment.type.PaymentErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class PaymentException extends RuntimeException {
    private final PaymentErrorCode paymentErrorCode;

    public PaymentException(PaymentErrorCode paymentErrorCode){
        super(paymentErrorCode.getMessage());
        this.paymentErrorCode = paymentErrorCode;
    }
}
