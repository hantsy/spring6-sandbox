package com.example.demo.domain;

import com.example.demo.JdbcConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {JdbcConfig.class})
class TestConfig {
}
