package com.uzem.book_cycle.cart.dto;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.admin.type.SalesStatus;
import com.uzem.book_cycle.cart.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {

    private Long cartId;
    private Long price;
    private String title;
    private SalesStatus salesStatus;
    private RentalStatus rentalStatus;

    public static CartResponseDTO fromSales(Cart cart, SalesBook salesBook) {
        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .price(salesBook.getPrice())
                .title(salesBook.getTitle())
                .salesStatus(salesBook.getSalesStatus())
                .build();
    }

    public static CartResponseDTO fromRental(Cart cart, RentalBook rentalBook) {
        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .price(rentalBook.getPrice())
                .title(rentalBook.getTitle())
                .rentalStatus(rentalBook.getRentalStatus())
                .build();
    }
}
