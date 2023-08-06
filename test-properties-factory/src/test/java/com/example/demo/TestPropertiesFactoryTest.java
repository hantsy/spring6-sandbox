package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = Config.class)
//@TestPropertySource(value = "/test.yaml", factory = YamlPropertySourceFactory.class)
@YamlTestProperties("/test.yaml")
public class TestPropertiesFactoryTest {
    private static final Logger log= LoggerFactory.getLogger(TestPropertiesFactoryTest.class);

    @Autowired
    Environment environment;

    @Test
    public void testProperties() {
        String title = environment.getProperty("blog.title");
        log.debug("yaml value blog.title: {}", title);
        assertThat(title).isEqualTo("Forward Everyday");
    }
}

@Configuration
class Config{}