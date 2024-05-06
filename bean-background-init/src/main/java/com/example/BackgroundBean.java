package com.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
@RequiredArgsConstructor
public class BackgroundBean  implements InitializingBean {
    private final BackgroundDependentBean backgroundDependentBean;

    @Override
    public void afterPropertiesSet() {
        log.debug("initializing BackgroundBean via thread:{} ", Thread.currentThread().getName());
    }
}
