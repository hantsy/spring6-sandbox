package com.example.demo.client;

import com.example.demo.shared.PostApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient(ObjectMapper objectMapper) {
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, ClientResponse::createException)
                .build();
    }

    @Bean
    PostApi postClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builder()
                        .exchangeAdapter(WebClientAdapter.create(webClient))
                        .conversionService(new DefaultFormattingConversionService())
                        .build();
        return httpServiceProxyFactory.createClient(PostApi.class);
    }

}
