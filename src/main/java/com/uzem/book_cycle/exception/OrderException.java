package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.order.type.OrderErrorCode;
import lombok.*;

@Getter
public class OrderException extends RuntimeException {
    private final OrderErrorCode orderErrorCode;

    public OrderException(OrderErrorCode orderErrorCode){
        super(orderErrorCode.getMessage());
        this.orderErrorCode = orderErrorCode;
    }
}
