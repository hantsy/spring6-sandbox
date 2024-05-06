package com.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
@RequiredArgsConstructor
public class LazyBean implements InitializingBean {
    private final LazyDependentBean lazyDependentBean;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("initializing LazyBean via thread:{} ", Thread.currentThread().getName());
    }
}
