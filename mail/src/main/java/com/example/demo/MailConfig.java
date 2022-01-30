package com.example.demo;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource(value = "classpath:mail.properties")
public class MailConfig implements EnvironmentAware {
    private Environment env;

    @Bean
    JavaMailSenderImpl javaMailSender() {
        var sender = new JavaMailSenderImpl();
        sender.setHost(env.getProperty("mail.host"));
        sender.setPort(env.getProperty("mail.port", Integer.class));
        sender.setProtocol(env.getProperty("mail.protocol"));
        // user and password
        sender.setUsername(env.getProperty("mail.username"));
        sender.setPassword(env.getProperty("mail.password"));
        return sender;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
