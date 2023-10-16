package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@ComponentScan(basePackageClasses = Application.class)
@Slf4j
public class Application {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(Application.class);
        var posts = context.getBean(PostRepository.class);
        posts.findAll().forEach(post -> log.debug("get the initial posts: {}", post));
        System.out.printf("... the end...");
    }
}
