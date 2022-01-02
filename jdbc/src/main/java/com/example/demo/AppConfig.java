package com.example.demo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@ComponentScan(
        basePackageClasses = AppConfig.class,
        useDefaultFilters = true,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^(com\\.example\\.demo\\.web\\.)(.+)$")
        }
)
public class AppConfig {
}
