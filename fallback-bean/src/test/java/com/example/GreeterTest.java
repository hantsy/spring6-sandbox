package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = Config.class)
class GreeterTest {

    @Autowired
    Printer printer;

    @Test
    public void testCustomerService() {
        this.printer.print();
        assertThat(this.printer.getMessage()).isEqualTo("Hello fallback");
    }
}