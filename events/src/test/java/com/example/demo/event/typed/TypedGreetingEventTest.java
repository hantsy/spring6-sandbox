package com.example.demo.event.typed;

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

@SpringJUnitConfig(classes = TypedGreetingEventTest.TestConfig.class)
@RecordApplicationEvents
public class TypedGreetingEventTest {

    @Autowired
    ApplicationEvents applicationEvents;

    @Autowired
    GreetingPublisher publisher;

    @Configuration
    @ComponentScan(basePackageClasses = Greeting.class)
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
