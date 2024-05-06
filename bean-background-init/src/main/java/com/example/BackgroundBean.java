package com.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
@RequiredArgsConstructor
public class BackgroundBean  implements InitializingBean {
    private final BackgroundDependentBean backgroundDependentBean;

    @Override
    public void afterPropertiesSet() throws InterruptedException {
        log.debug("initializing BackgroundBean via thread:{} ", Thread.currentThread().getName());
        Thread.sleep(500);
        log.debug("end of initializing BackgroundBean after 500ms via thread:{} ", Thread.currentThread().getName());
    }
}
