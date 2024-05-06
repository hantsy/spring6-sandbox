package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class BackgroundDependentBean  implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        log.debug("initializing BackgroundDependentBean via thread:{} ", Thread.currentThread().getName());
    }
}
