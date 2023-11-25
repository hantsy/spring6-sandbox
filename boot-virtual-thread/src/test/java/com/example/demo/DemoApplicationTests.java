package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(properties = "spring.threads.virtual.enabled=false")
@ActiveProfiles("test")
class DemoApplicationTests {

    @Autowired
    ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context.getBean(GreetingListener.class)).isNotNull();
        assertThatThrownBy(() -> context.getBean(AsyncGreetingListener.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
    }

}
