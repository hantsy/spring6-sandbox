package com.example.demo.testcontainers;

import com.example.demo.domain.JpaConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = JpaConfig.class)
class TestConfig {
}
