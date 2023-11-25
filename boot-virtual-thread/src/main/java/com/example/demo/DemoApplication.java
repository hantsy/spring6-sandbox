package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.task.SimpleAsyncTaskExecutorCustomizer;
import org.springframework.boot.task.SimpleAsyncTaskSchedulerCustomizer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@Slf4j
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


//    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
//    public AsyncTaskExecutor asyncTaskExecutor() {
//        return new SimpleAsyncTaskExecutor("vt-");
//       return new VirtualThreadTaskExecutor("");
//    }
//    @Bean
//    public AsyncTaskExecutor asyncTaskExecutor(SimpleAsyncTaskExecutorBuilder builder) {
//        return builder.threadNamePrefix("vt-").virtualThreads(true).build();
//    }

    @Bean
    public SimpleAsyncTaskExecutorCustomizer asyncTaskExecutorCustomizer() {
        return taskExecutor -> taskExecutor.setTaskDecorator(runnable -> {
            log.debug("running task decorator:");
            return runnable;
        });
    }

    @Bean
    public SimpleAsyncTaskSchedulerCustomizer asyncTaskSchedulerCustomizer() {
        return taskScheduler -> taskScheduler.setTaskDecorator(runnable -> {
            log.debug("running task decorator:");
            return runnable;
        });
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner dataInitializer(ApplicationEventPublisher publisher) {
        return args -> {
            IntStream.rangeClosed(1, 100)
                    .forEachOrdered(i ->
                            {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                Greeting greeting = new Greeting("Hello #" + i + " at:" + LocalDateTime.now());
                                log.debug("sending message: {}", greeting);
                                publisher.publishEvent(greeting);
                            }
                    );
        };
    }

    @Bean
    @ConditionalOnThreading(Threading.VIRTUAL)
    public AsyncGreetingListener asyncGreetingListener() {
        return new AsyncGreetingListener();
    }


    @Bean
    @ConditionalOnThreading(Threading.PLATFORM)
    public GreetingListener greetingListener() {
        return new GreetingListener();
    }

}
