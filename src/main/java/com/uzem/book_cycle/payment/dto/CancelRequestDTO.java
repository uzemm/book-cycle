package com.uzem.book_cycle.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CancelRequestDTO {

    @NotBlank(message = "paymentKey는 필수입니다.")
    private String paymentKey;
    @NotBlank(message = "취소 사유는 필수입니다.")
    private String cancelReason;
    @Min(value = 1, message = "취소 금액은 1원 이상이어야 합니다.")
    private Long cancelAmount;
}
