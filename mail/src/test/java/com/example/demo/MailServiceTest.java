package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@SpringJUnitConfig(value = TestConfig.class)
public class MailServiceTest {

    @Autowired
    MailService mailService;

    @Autowired
    JavaMailSenderImpl mailSender;

    @AfterEach
    public void teardown() {
        reset(mailSender);
    }

    @Test
    public void testSendSimpleMailMessage(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        mailService.sendSimpleMailMessage("test@examle.com", "test", "test email");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verifyNoMoreInteractions(mailSender);
    }
}
