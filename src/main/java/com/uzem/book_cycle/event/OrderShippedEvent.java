package com.uzem.book_cycle.event;


public record OrderShippedEvent(Long orderId, String message) {
}
