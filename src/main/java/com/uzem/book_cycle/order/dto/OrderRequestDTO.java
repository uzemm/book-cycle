package com.uzem.book_cycle.order.dto;

import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.type.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotBlank(message = "수령인 이름을 입력하세요.")
    private String receiverName;
    @NotBlank
    private String receiverZipcode;
    @NotBlank
    private String receiverAddress;
    @NotBlank
    @Pattern(regexp = "^01[016-9][0-9]{7,8}$",
            message = "전화번호는 숫자만 입력해주세요.")
    private String receiverPhone;
    private String deliveryMessage;
    @NotNull
    private PaymentMethod paymentMethod;
    private Long usedPoint;

    private List<OrderItemRequestDTO> orderItems; // 바로구매일 때만 채움

    private PaymentRequestDTO payment;

    private List<Long> cartIds;

    public boolean isCartOrder() {
        return (this.orderItems == null || this.orderItems.isEmpty())
                && (this.cartIds != null && !this.cartIds.isEmpty());
    }

}
