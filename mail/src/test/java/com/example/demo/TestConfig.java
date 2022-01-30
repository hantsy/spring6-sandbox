package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan(basePackageClasses = {MailConfig.class})
class TestConfig {

    @Bean
    @Primary
    JavaMailSenderImpl mockJavaMailSender() {
        return mock(JavaMailSenderImpl.class);
    }
}
