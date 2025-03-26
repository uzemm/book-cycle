package com.uzem.book_cycle.admin.dto.rental;


import com.uzem.book_cycle.admin.dto.UpdateBookRequestDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminRentalRequestDTO extends UpdateBookRequestDTO {
    @NotNull
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Long price;
}
