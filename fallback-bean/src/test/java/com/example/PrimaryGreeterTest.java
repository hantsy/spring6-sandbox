package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {PrimaryGreeterConfig.class, DummyConfig.class})
class PrimaryGreeterTest {

    @Autowired
    Greeter greeter;

    @Test
    public void testCustomerService() {
        assertThat(this.greeter.greet()).isEqualTo("Hello primary");
    }
}