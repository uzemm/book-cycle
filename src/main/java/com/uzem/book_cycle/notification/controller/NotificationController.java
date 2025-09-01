package com.uzem.book_cycle.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class NotificationController{
    private final SimpMessagingTemplate messagingTemplate;

}
