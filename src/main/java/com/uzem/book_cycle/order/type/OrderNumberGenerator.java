package com.uzem.book_cycle.order.type;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderNumberGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String createOrderNumber() {
        return "BC"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + (100000  + random.nextInt(900000));
    }
}
