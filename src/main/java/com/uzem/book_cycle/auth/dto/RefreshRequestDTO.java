package com.uzem.book_cycle.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RefreshRequestDTO {
    private String refreshToken;
    private String email;
}
