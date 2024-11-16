package com.example.custom;

import org.springframework.core.ResolvableType;
import org.springframework.test.context.bean.override.BeanOverrideHandler;
import org.springframework.test.context.bean.override.BeanOverrideProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

class StubBeanOverrideProcessor implements BeanOverrideProcessor {
    @Override
    public BeanOverrideHandler createHandler(Annotation overrideAnnotation, Class<?> testClass, Field field) {
        if (overrideAnnotation instanceof StubBean stubBean) {
            return new StubBeanOverrideHandler(stubBean, field, ResolvableType.forField(field, testClass));
        }
        throw new IllegalArgumentException("Make sure the bean to override is annotated with @StubBean");
    }
}
