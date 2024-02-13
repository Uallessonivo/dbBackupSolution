package com.project.dbbackupsolution.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Getter
@Configuration
public class EmailNotificationConfig {
    @Value("${mail.smtp.host}")
    private String host;
    @Value("${mail.smtp.port}")
    private String port;
    @Value("${mail.smtp.username}")
    private String username;
    @Value("${mail.smtp.password}")
    private String password;

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "*");
        return properties;
    }
}
