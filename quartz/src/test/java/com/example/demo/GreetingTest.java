package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(TestConfig.class)
public class GreetingTest {

    @Autowired
    Greeting greetingSpy;

    //Greeting greetingSpy;

    @BeforeEach
    public void setUp() {
        //this.greetingSpy = spy(greeting);
    }

    @Test
    public void checkGreetingSpy() {
        assertTrue(Mockito.mockingDetails(greetingSpy).isSpy());
    }

    @Test
    public void whenWaitTenSecond_thenScheduledIsCalledAtLeastTenTimes() {
        await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> verify(greetingSpy, atLeast(1)).sayHello());
    }

    @Test
    public void testNextExecutingTime() {
        var next = CronExpression.parse("*/10 * * * * *")
                .next(LocalDateTime.of(2022, 2, 1, 0, 0, 0));
        assert next != null;
        assertThat(next.getSecond()).isEqualTo(10);
    }
}
