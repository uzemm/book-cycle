package com.uzem.book_cycle.cart.dto;

import com.uzem.book_cycle.order.type.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "장바구니 요청 DTO")
public class CartRequestDTO {

    @NotNull
    @Schema(description = "도서id", example = "1")
    private Long bookId;

    @Schema(description = "도서타입", example = "SALE/RENTAL")
    @NotNull
    private ItemType itemType;

}
