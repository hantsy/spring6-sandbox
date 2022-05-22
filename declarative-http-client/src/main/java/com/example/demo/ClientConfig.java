package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }


    @Bean
    PostClient postClient(WebClient webClient) {
        var httpServiceProxyFactory = HttpServiceProxyFactory
                .builder(new WebClientAdapter(webClient))
                .setConversionService(new DefaultFormattingConversionService())
                .build();
        return httpServiceProxyFactory.createClient(PostClient.class);
    }

}
