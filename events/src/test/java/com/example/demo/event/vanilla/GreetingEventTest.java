package com.example.demo.event.vanilla;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

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
        assertThat(applicationEvents.stream(GreetingEvent.class).count()).isEqualTo(1);
    }
}
