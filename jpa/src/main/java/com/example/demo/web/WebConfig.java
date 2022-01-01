package com.example.demo.web;

import com.example.demo.AppConfig;
import com.example.demo.domain.JpaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import org.springframework.context.annotation.Import;

@Configuration
@EnableWebMvc
@ComponentScan(
    basePackageClasses = WebConfig.class,
    useDefaultFilters = false,
    includeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ANNOTATION,
          classes = {RestController.class, RestControllerAdvice.class}
      )
    }
)
@Import(AppConfig.class)
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  ObjectMapper objectMapper;

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    var jackson2MessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
    converters.add(jackson2MessageConverter);
  }
}
