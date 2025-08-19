package com.uzem.book_cycle.admin.dto.rental;

import com.uzem.book_cycle.admin.type.RentalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AdminRentalStatusDTO {

    private Long memberId;
    private String memberName;
    private String bookTitle;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Long overdueFee;
    private RentalStatus rentalStatus;
    private boolean isOverduePayment;
    private String orderNumber;
}
