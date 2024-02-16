package com.project.dbbackupsolution.infrastructure;

import com.project.dbbackupsolution.configuration.EmailNotificationConfig;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotification {
    private final EmailNotificationConfig emailNotificationConfig;
    private final JavaMailSender emailSender;

    public EmailNotification(EmailNotificationConfig emailNotificationConfig, JavaMailSender emailSender) {
        this.emailNotificationConfig = emailNotificationConfig;
        this.emailSender = emailSender;
    }

    public void sendEmail(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailNotificationConfig.getUsername());
        message.setTo(emailNotificationConfig.getDestinationEmail());
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
