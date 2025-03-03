package com.uzem.book_cycle.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RefreshRequestDTO {
    @NotBlank(message = "Refresh token must not be blank")
    private String refreshToken;
}
