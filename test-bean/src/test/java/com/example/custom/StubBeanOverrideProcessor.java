package com.example.custom;

import org.springframework.core.ResolvableType;
import org.springframework.test.context.bean.override.BeanOverrideProcessor;
import org.springframework.test.context.bean.override.OverrideMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

class StubBeanOverrideProcessor implements BeanOverrideProcessor {
    @Override
    public OverrideMetadata createMetadata(Annotation overrideAnnotation, Class<?> testClass, Field field) {
        if (overrideAnnotation instanceof StubBean stubBean) {
            return new StubBeanMetadata(stubBean, field, ResolvableType.forField(field, testClass));
        }
        throw new IllegalArgumentException("Make sure the bean to override is annotated with @StubBean");
    }
}
