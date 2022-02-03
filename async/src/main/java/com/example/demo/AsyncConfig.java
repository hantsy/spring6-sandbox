package com.example.demo;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean
    @Override
    public Executor getAsyncExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    // global enable async on event publishing and listening.
//    @Bean(name = "applicationEventMulticaster")
//    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
//        SimpleApplicationEventMulticaster eventMulticaster =
//                new SimpleApplicationEventMulticaster();
//
//        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        return eventMulticaster;
//    }
}
