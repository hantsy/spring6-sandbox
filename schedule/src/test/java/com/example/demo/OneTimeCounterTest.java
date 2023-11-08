package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringJUnitConfig(value = OneTimeCounterTest.OneTimeConfig.class)
public class OneTimeCounterTest {

    @Configuration
    @Import({OneTimeCounter.class, ScheduleConfig.class})
    static class OneTimeConfig {
    }

    @Autowired
    OneTimeCounter counter;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void counterOnlyIncreaseOnce() {

        await().atMost(Duration.ofMillis(1000)).untilAsserted(
                () -> assertThat(counter.getInvocationCount()).isEqualTo(1)
        );

        await().atMost(Duration.ofMillis(1500)).untilAsserted(
                () -> assertThat(counter.getInvocationCount()).isEqualTo(1)
        );

        await().atMost(Duration.ofMillis(2000)).untilAsserted(
                () -> assertThat(counter.getInvocationCount()).isEqualTo(1)
        );
    }
}

