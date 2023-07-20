package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(value = TestConfig.class)
public class CounterTest {

    @Autowired
    Counter counter;

    //Counter counterSpy;
    @BeforeEach
    public void setUp() {
        //counterSpy = spy(counter);
    }

    @Test
    public void givenSleepBy100ms_whenGetInvocationCount_thenIsGreaterThanZero()
            throws InterruptedException {
        Thread.sleep(100L);
        assertThat(counter.getInvocationCount()).isGreaterThan(0);
    }

    @Test
    public void whenWaitOneSecond_thenScheduledIsCalledAtLeastTenTimes() {
        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(counter, atLeast(10)).scheduled());
    }

    @Test
    public void testNextExecutingTime() {
        var next = CronExpression.parse("*/10 * * * * *").next(LocalDateTime.of(2022, 2, 1, 0, 0, 0));
        assertThat(next.getSecond()).isEqualTo(10);
    }
}
