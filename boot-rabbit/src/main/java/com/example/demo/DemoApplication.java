package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    public final static String QUEUE_HELLO = "q.hello";
    public final static String EXCHANGE_HELLO = "x.hello";
    public final static String ROUTING_HELLO = "r.hello";

    @Bean
    Queue helloQueue() {
        return new Queue(QUEUE_HELLO, true);
    }

    @Bean
    DirectExchange helloExchange() {
        return new DirectExchange(EXCHANGE_HELLO);
    }

    @Bean
    Binding helloBinding() {
        return BindingBuilder.bind(helloQueue()).to(helloExchange()).with(ROUTING_HELLO);
    }

    @Bean
    MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}
