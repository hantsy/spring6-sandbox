package com.example.demo.event.vanilla;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = GreetingEventTest.TestConfig.class)
@RecordApplicationEvents
public class GreetingEventTest {

    @Autowired
    ApplicationEvents applicationEvents;

    @Autowired
    GreetingEventPublisher publisher;

    @Configuration
    @ComponentScan(basePackageClasses = GreetingEvent.class)
    static class TestConfig {

    }

    @Test
    public void testGreetingEvents() {
        publisher.publishGreetingEvent("hello world");

        Awaitility.await().atMost(Duration.ofMillis(500))
                .untilAsserted(() ->
                        assertThat(
                                applicationEvents.stream()
                                        .count()
                        )
                                .isEqualTo(1)
                );
    }
}
