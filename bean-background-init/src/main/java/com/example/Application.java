package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@Configuration
@ComponentScan
@EnableAsync
@Slf4j
public class Application {

    @Bean
    public Executor bootstrapExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("bootstrap-");
        return executor;
    }

    @Bean
    public BackgroundDependentBean backgroundDependentBean() {
        return new BackgroundDependentBean();
    }

    @Bean(bootstrap = Bean.Bootstrap.BACKGROUND)
    public BackgroundBean backgroundBean(BackgroundDependentBean backgroundDependentBean) {
        return new BackgroundBean(backgroundDependentBean);
    }

    @Bean
    public LazyDependentBean lazyDependentBean() {
        return new LazyDependentBean();
    }

    @Bean
    @Lazy
    public LazyBean lazyBean(LazyDependentBean lazyDependentBean) {
        return new LazyBean(lazyDependentBean);
    }

    @Bean
    public SampleListener sampleListener() {
        return new SampleListener();
    }

    public static void main(String[] args) {
        log.debug("refreshing application context via thread: {}", Thread.currentThread().getName());
        ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        log.debug("end of refreshing application context via thread: {}", Thread.currentThread().getName());
        var lazyBean = context.getBean(LazyBean.class);
        var backgroudBean = context.getBean(BackgroundBean.class);
    }
}
