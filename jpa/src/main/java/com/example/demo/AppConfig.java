package com.example.demo;

import com.example.demo.domain.JpaConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Configuration
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@ComponentScan(
        basePackageClasses = AppConfig.class,
        useDefaultFilters = true,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ANNOTATION,
                        classes = {Configuration.class, RestController.class, RestControllerAdvice.class}
                )
        }
)
@Import(JpaConfig.class)
public class AppConfig {
}
