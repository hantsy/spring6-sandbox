package com.example.demo.client;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.service.RSocketServiceProxyFactory;

import java.time.Duration;

@Configuration
public class ClientConfig {


    @Bean
    RSocketRequester rSocketRequester(RSocketStrategies strategies) {
        return RSocketRequester.builder()
                .rsocketStrategies(strategies)
                .tcp("localhost", 7000);
    }

    @SneakyThrows
    @Bean
    public PostClientService postClientService(RSocketRequester requester) {
        RSocketServiceProxyFactory rSocketServiceProxyFactory =
                RSocketServiceProxyFactory.builder()
                        .rsocketRequester(requester)
                        .blockTimeout(Duration.ofMillis(5000))
                        .build();
        return rSocketServiceProxyFactory.createClient(PostClientService.class);
    }
}
