package com.example.demo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Counter {
    private AtomicInteger count = new AtomicInteger(0);

    @Scheduled(fixedDelay = 5)
    //
    // with a fixed rate.
    // @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    //
    // with and initial delay a fixed rate.
    // @Scheduled(initialDelay = 1000, fixedRate = 5000)
    //
    // with a cron expression
    // @Scheduled(cron="*/5 * * * * MON-FRI")
    //
    // with a cron marco
    // @Scheduled(cron="@daily")
    public void scheduled() {
        this.count.incrementAndGet();
    }

    public int getInvocationCount() {
        return this.count.get();
    }
}