package com.example.demo.client;

import com.example.demo.shared.PostApi;
import com.example.demo.shared.PostNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;
import java.util.UUID;

@Configuration
public class ClientConfig {
    private static final Logger log = LoggerFactory.getLogger(ClientConfig.class);

    @Bean
    WebClient webClient(WebClient.Builder builder, ObjectMapper objectMapper) {
        return builder
                .baseUrl("http://localhost:8080")
                // 1. throw exception and handle in the certain calling process
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, ClientResponse::createError)

                // 2. wrap it into a client friendly exception
                //.defaultStatusHandler(HttpStatusCode::is4xxClientError,
                //        response -> response.createException()
                //                 .map(it -> new PostServiceException(it.getResponseBodyAsString()))

                // 3. restore it into the original exception in the shared module
                //.defaultStatusHandler(status -> status == HttpStatus.NOT_FOUND,
                //        response -> response.createException()
                //                .map(it -> {
                //                    ProblemDetail problemDetails = null;
                //                    try {
                //                        problemDetails = objectMapper.readValue(it.getResponseBodyAsByteArray(), ProblemDetail.class);
                //                    } catch (IOException e) {
                //                        throw new RuntimeException(e);
                //                    }
                //                    log.debug("extracting exception body to problem details: {}", problemDetails);
                //
                //                    return new PostNotFoundException(UUID.fromString(problemDetails.getProperties().get("id").toString()));
                //                })
                //)
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
