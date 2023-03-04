package com.example.demo;

import jakarta.annotation.Resource;
import jakarta.mail.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Resource(lookup ="java:comp/env/mail/Session")
    Session session;

    @Bean
    JavaMailSenderImpl javaMailSender() {
        var sender = new JavaMailSenderImpl();
        sender.setSession(session);
        return sender;
    }
}
