package com.uzem.book_cycle.admin.dto.sales;

import com.uzem.book_cycle.admin.dto.UpdateBookRequestDTO;
import com.uzem.book_cycle.admin.type.BookQuality;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSalesRequestDTO extends UpdateBookRequestDTO {
    @NotNull
    private int price;
    @NotNull
    private BookQuality bookQuality;

}
