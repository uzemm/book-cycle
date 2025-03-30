package com.uzem.book_cycle.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveAmountRequest {

    @NotBlank
    private Long amount;
    @NotBlank
    private String orderId;
}
