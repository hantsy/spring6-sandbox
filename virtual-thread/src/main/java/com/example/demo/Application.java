package com.example.demo;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan
@Configuration
@Slf4j
public class Application {
    @SneakyThrows
    public static void main(String[] args) {
        var applicationContext = new AnnotationConfigApplicationContext(Application.class);
        var greetingListener = applicationContext.getBean(GreetingListener.class);

        Thread.sleep(500);
        log.debug("The event listener invocation count:{}", greetingListener.getInvocationCount());
    }

    @Bean
    public GreetingListener greetingListener() {
        return new GreetingListener();
    }

    @Bean
    public GreetingOneTimeScheduler greetingOneTimeScheduler() {
        return new GreetingOneTimeScheduler();
    }

    @Bean
    public GreetingInitializer greetingInitializer() {
        return new GreetingInitializer();
    }
}
