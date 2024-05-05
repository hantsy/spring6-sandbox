package com.example.custom;

import org.springframework.test.context.bean.override.BeanOverride;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanOverride(StubBeanOverrideProcessor.class)
public @interface StubBean {
    Class<?> value();
}
