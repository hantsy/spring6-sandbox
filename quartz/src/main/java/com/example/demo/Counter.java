package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Counter extends QuartzJobBean {
    private AtomicInteger count = new AtomicInteger(0);

    public int getInvocationCount() {
        return this.count.get();
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        this.count.incrementAndGet();
        log.debug("count is: {}", this.getInvocationCount());
    }
}