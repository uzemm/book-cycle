package com.uzem.book_cycle.external.email.service;


import org.thymeleaf.context.Context;

public interface EmailSender {

    void sendEmail(String to, String subject, String templateName, Context context);
}
