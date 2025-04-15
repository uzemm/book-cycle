package com.uzem.book_cycle.wish.dto;

import com.uzem.book_cycle.admin.type.SalesStatus;
import com.uzem.book_cycle.wish.entity.Wish;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class WishResponseDTO {

    private Long wishId;
    private String title;
    private Long price;
    private LocalDate createdAt;
    private SalesStatus salesStatus;

    public static WishResponseDTO from(Wish wish) {
        return WishResponseDTO.builder()
                .wishId(wish.getId())
                .title(wish.getSalesBook().getTitle())
                .price(wish.getSalesBook().getPrice())
                .createdAt(LocalDate.now())
                .salesStatus(wish.getSalesBook().getSalesStatus())
                .build();
    }
}
