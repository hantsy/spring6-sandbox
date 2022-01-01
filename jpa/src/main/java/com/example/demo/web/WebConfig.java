package com.example.demo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  ObjectMapper objectMapper;

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    var jackson2MessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
    converters.add(jackson2MessageConverter);
  }
}
