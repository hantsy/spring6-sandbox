package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(value = {
        JmsConfig.class,
        Sender.class,
        Receiver.class
})
public class JmsTest {

    @Autowired
    Sender sender;

    @Autowired
    Receiver receiver;

    @Test
    public void whenWaitOneSecond_thenReceiverShouldReceiveAllMessages() {
        sender.send();

        // wait one second to verify.
        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> assertThat(receiver.getMessageList().size()).isEqualTo(10));
    }
}
