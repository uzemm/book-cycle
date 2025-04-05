package com.uzem.book_cycle.order.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderErrorCode {
    ORDER_NOT_FOUND("주문내역을 찾을 수 없습니다."),
    INVALID_TOTAL_PRICE("총 결제 금액은 0원보다 커야 합니다."),
    ORDER_ITEM_NOT_FOUND("주문 항목이 없습니다."),
    DUPLICATE_ORDER("이미 처리된 주문입니다.")
    ;

    private String description;
}
