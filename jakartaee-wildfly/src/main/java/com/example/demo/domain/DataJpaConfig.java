package com.example.demo.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@Configuration
@EnableJpaRepositories
@EnableJpaAuditing
public class DataJpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("hantsy");
    }
}
