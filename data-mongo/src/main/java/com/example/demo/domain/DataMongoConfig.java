package com.example.demo.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories
public class DataMongoConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("hantsy");
    }
}
