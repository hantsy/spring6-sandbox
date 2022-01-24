package com.example.demo.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.util.Optional;

@Configuration
@EnableJdbcRepositories
@EnableJdbcAuditing
public class DataJdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("hantsy");
    }
}
