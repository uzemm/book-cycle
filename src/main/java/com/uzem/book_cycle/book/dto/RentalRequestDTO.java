package com.uzem.book_cycle.book.dto;

import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RentalRequestDTO {

    @NotNull
    private Long rentalBookId; // 대여도서 id
    private PaymentRequestDTO payment; // 결제 DTO
}
