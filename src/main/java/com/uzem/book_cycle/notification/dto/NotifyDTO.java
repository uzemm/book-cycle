package com.uzem.book_cycle.notification.dto;

import com.uzem.book_cycle.notification.type.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotifyDTO {

    private NotificationType type;
    private String message;
}
