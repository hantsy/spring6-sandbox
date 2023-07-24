package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    RestClient restClient(ObjectMapper objectMapper) {
        return RestClient.builder()
                .baseUrl("http://localhost:8080")
                .messageConverters(converters -> {
                            converters.clear();
                            converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
                        }
                )
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
    }

}
