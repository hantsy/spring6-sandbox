package com.example.custom;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.bean.override.BeanOverrideStrategy;
import org.springframework.test.context.bean.override.OverrideMetadata;

import java.lang.reflect.Field;

public class StubBeanMetadata extends OverrideMetadata {
    private StubBean stubBean;

    public StubBeanMetadata(StubBean stubBean, Field field, ResolvableType resolvableType) {
        super(field, resolvableType, BeanOverrideStrategy.REPLACE_OR_CREATE_DEFINITION);
        this.stubBean = stubBean;
    }

    @SneakyThrows
    @Override
    protected Object createOverride(String beanName, BeanDefinition existingBeanDefinition, Object existingBeanInstance) {
        // create a stub object...
        return stubBean.value().getDeclaredConstructor().newInstance();
    }
}
