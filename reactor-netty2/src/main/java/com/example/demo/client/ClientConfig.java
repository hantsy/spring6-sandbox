package com.example.demo.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty5.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorNetty2ClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty5.http.client.HttpClient;

import java.util.concurrent.Executors;

@Configuration
public class ClientConfig {
    @Bean
    WebClient webClient(ObjectMapper objectMapper) {

        var httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000);
        var clientConnector = new ReactorNetty2ClientHttpConnector(httpClient);
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .clientConnector(clientConnector)
                .codecs(clientCodecConfigurer -> {
                            clientCodecConfigurer
                                    .defaultCodecs()
                                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                            clientCodecConfigurer
                                    .defaultCodecs()
                                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                        }
                )
                .build();
    }

    @Bean
    HelloClient postClient(WebClient webClient) {
        return new HelloClient(webClient);
    }
}
