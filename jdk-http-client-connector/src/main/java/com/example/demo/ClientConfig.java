package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient(ObjectMapper objectMapper) {

        var jvmHttpClient = HttpClient.newBuilder()
                .executor(Executors.newCachedThreadPool())
                .version(HttpClient.Version.HTTP_2)
                .build();
        var clientConnector = new JdkClientHttpConnector(jvmHttpClient);
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
    PostClient postClient(WebClient webClient) {
        return new PostClient(webClient);
    }

}
