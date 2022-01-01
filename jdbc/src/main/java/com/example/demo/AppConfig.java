package com.example.demo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
public class AppConfig {
}
