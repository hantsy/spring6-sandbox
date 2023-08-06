package com.example.demo;


import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestPropertySource(factory = YamlPropertySourceFactory.class)
public @interface YamlTestProperties {

    @AliasFor(annotation = TestPropertySource.class)
    String[] value();
}
