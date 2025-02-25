package com.example.custom;

import com.example.Config;
import com.example.CustomerService;
import com.example.DummyCustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {Config.class, CustomConfig.class})
class CustomerServiceStubBeanTest {

    @StubBean(DummyCustomerService.class)
    CustomerService testCustomerService;

    @Test
    public void testCustomerService() {
        var testCustomer = testCustomerService.findByEmail("dummy@example.com");
        assertThat(testCustomer.firstName()).isEqualTo("dummy first");
        assertThat(testCustomer.lastName()).isEqualTo("dummy last");
        assertThat(testCustomerService.findAll()).isEmpty();
    }
}