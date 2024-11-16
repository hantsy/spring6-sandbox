package com.example.custom;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.bean.override.BeanOverrideHandler;
import org.springframework.test.context.bean.override.BeanOverrideStrategy;

import java.lang.reflect.Field;

public class StubBeanOverrideHandler extends BeanOverrideHandler {
    private static Logger log = LoggerFactory.getLogger(StubBeanOverrideHandler.class);
    private StubBean stubBean;

    public StubBeanOverrideHandler(StubBean stubBean, Field field, ResolvableType resolvableType) {
        super(field, resolvableType, null, BeanOverrideStrategy.REPLACE_OR_CREATE);
        this.stubBean = stubBean;
    }

    @SneakyThrows
    @Override
    protected Object createOverrideInstance(String beanName, BeanDefinition existingBeanDefinition, Object existingBeanInstance) {
        // create a stub object...
        return stubBean.value().getDeclaredConstructor().newInstance();
    }

    @Override
    protected void trackOverrideInstance(Object override, SingletonBeanRegistry singletonBeanRegistry) {
        log.debug("track override instance, override: {}, singleton bean registry: {}", override, singletonBeanRegistry);
    }
}
