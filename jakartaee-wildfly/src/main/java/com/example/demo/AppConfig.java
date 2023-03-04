package com.example.demo;

import jakarta.annotation.Resource;
import jakarta.mail.Session;
import org.springframework.context.annotation.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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
public class AppConfig {


}
