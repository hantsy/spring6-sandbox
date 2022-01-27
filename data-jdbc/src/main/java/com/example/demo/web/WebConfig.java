package com.example.demo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
@EnableWebMvc
@ComponentScan(
        basePackageClasses = WebConfig.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern ="^(com\\.example\\.demo\\.web\\.)(.*Handler)$")
        }
)
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        var jackson2MessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        converters.add(jackson2MessageConverter);
    }

    @Bean
    public RouterFunction<ServerResponse> routes(PostHandler postHandler) {
        return route()
                .GET("/posts", postHandler::all)
                .POST("/posts", postHandler::create)
                .GET("/posts/{id}", postHandler::get)
                .PUT("/posts/{id}", postHandler::update)
                .DELETE("/posts/{id}", postHandler::delete)
                .build();
    }
}
