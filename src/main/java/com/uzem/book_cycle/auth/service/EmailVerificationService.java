package com.uzem.book_cycle.auth.service;

import com.uzem.book_cycle.external.email.service.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailSender emailSender;

    public void sendVerification(String to, String verificationCode) {
        String subject = "책이음 이메일 인증";
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);

        emailSender.sendEmail(to, subject, "email", context);
    }

    public void sendChangeEmailVerification(String to, String verificationCode) {
        String subject = "책이음 이메일 변경 인증";
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);

        emailSender.sendEmail(to, subject, "email-change", context);
    }

}
