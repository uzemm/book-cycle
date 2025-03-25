package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.order.type.OrderErrorCode;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderException extends RuntimeException {
    private OrderErrorCode orderErrorCode;
    private String errorMessage;

    public OrderException(OrderErrorCode orderErrorCode){
        this.orderErrorCode = orderErrorCode;
        this.errorMessage = orderErrorCode.getDescription();
    }
}
