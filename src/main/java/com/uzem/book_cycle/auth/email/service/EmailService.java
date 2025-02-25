package com.uzem.book_cycle.auth.email.service;

import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.type.MemberErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String templateName, Context context) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("이메일 전송 성공: {}", to);
        } catch (MessagingException e) {
            log.info("이메일 전송 실패: {}", to, e);
            throw new MemberException(MemberErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void sendVerification(String to, String verificationCode) {
        String subject = "책이음 이메일 인증";
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);

        sendEmail(to, subject, "email", context);
    }

    public void sendChangeEmailVerification(String to, String verificationCode) {
        String subject = "책이음 이메일 변경 인증";
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);

        sendEmail(to, subject, "email-change", context);
    }

}
