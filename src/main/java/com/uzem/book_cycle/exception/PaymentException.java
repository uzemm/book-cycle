package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.payment.type.PaymentErrorCode;
import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {
    private final PaymentErrorCode paymentErrorCode;
    private final String originalMessage; // 토스 원본 메시지 (optional)

    public PaymentException(PaymentErrorCode paymentErrorCode){
        super(paymentErrorCode.getMessage());
        this.paymentErrorCode = paymentErrorCode;
        this.originalMessage = paymentErrorCode.getMessage();
    }

    public PaymentException(PaymentErrorCode paymentErrorCode, String originalMessage) {
        super(paymentErrorCode.getMessage() + " / " + originalMessage);
        this.paymentErrorCode = paymentErrorCode;
        this.originalMessage = originalMessage;
    }
}
