package com.project.dbbackupsolution.infrastructure;

import com.project.dbbackupsolution.configuration.EmailNotificationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotification {
    private final EmailNotificationConfig emailNotificationConfig;
    @Autowired
    private JavaMailSender emailSender;

    public EmailNotification(EmailNotificationConfig emailNotificationConfig) {
        this.emailNotificationConfig = emailNotificationConfig;
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
