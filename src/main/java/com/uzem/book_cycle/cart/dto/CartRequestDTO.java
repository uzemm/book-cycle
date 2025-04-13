package com.uzem.book_cycle.cart.dto;

import com.uzem.book_cycle.order.type.ItemType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDTO {

    @NotNull
    private Long bookId;
    @NotNull
    private ItemType itemType;

}
