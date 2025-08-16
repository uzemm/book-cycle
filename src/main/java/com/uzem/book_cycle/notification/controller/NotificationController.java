package com.uzem.book_cycle.notification.controller;

import com.uzem.book_cycle.notification.dto.NotifyDTO;
import com.uzem.book_cycle.notification.type.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static com.uzem.book_cycle.notification.type.NotificationType.RESERVATION_FIRST;

@Controller
@RequiredArgsConstructor
public class NotificationController{
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notify")
    public void handle(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        NotifyDTO dto = NotifyDTO.builder()
                .type(RESERVATION_FIRST)
                .message(RESERVATION_FIRST.getDefaultMessage())
                .build();

        messagingTemplate.convertAndSend("/sub/member/" + memberId, dto);
    }
}
