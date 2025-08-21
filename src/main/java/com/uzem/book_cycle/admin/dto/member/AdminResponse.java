package com.uzem.book_cycle.admin.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@AllArgsConstructor(staticName = "of")
public class AdminResponse {

    private String message;
    private final LocalDateTime timestamp;

    public static AdminResponse of(String message) {
        return new AdminResponse(message, LocalDateTime.now(ZoneOffset.UTC));
    }
}
