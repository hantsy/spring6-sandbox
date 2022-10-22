package com.example.demo.event.async;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = AsyncGreetingEventTest.TestConfig.class)
@RecordApplicationEvents
public class AsyncGreetingEventTest {

    @Autowired
    ApplicationEvents applicationEvents;

    @Autowired
    AsyncGreetingPublisher publisher;

    @Configuration
    @ComponentScan(basePackageClasses = AsyncGreeting.class)
    //@Import(AsyncConfig.class)  //Enabling async support will run listener on a different thread.
    static class TestConfig {

    }

    @Test
    public void testGreetingEvents() {
        publisher.publishGreetingEvent("hello world");
        assertThat(applicationEvents.stream(AsyncGreeting.class).count()).isEqualTo(1);
    }
}
