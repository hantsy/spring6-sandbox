package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient(ObjectMapper objectMapper) {
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
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

    @SneakyThrows
    @Bean
    PostClient postClient(WebClient webClient) {
        var httpServiceProxyFactory = new HttpServiceProxyFactory(new WebClientAdapter(webClient));
        httpServiceProxyFactory.setConversionService(new DefaultFormattingConversionService());
        httpServiceProxyFactory.afterPropertiesSet();
        return httpServiceProxyFactory.createClient(PostClient.class);
    }

}
