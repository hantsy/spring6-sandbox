package com.example.demo.client;

import io.rsocket.transport.netty.client.TcpClientTransport;
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
        RSocketRequester requester = RSocketRequester.builder()
            .rsocketStrategies(strategies)
            .tcp("localhost", 7000);
        return requester;
    }

    @SneakyThrows
    @Bean
    public PostClientService postClientService(RSocketRequester requester) {
        RSocketServiceProxyFactory rSocketServiceProxyFactory =
            RSocketServiceProxyFactory.builder(requester)
                .blockTimeout(Duration.ofMillis(5000))
                .build();
        return rSocketServiceProxyFactory.createClient(PostClientService.class);
    }
}
