package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
        taskRegistrar.addFixedDelayTask(
                () -> System.out.println("executing fixed delay task."),
                Duration.ofMillis(1000)
        );
    }

    @Bean()
    public Executor taskScheduler() {
        var taskScheduler = new SimpleAsyncTaskScheduler();
        taskScheduler.setVirtualThreads(true);
        taskScheduler.setThreadNamePrefix("schedule-vt-");
        return taskScheduler;
    }
}
