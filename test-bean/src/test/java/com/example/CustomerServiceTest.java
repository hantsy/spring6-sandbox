package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = Config.class)
class CustomerServiceTest {

    @Autowired
    CustomerService realCustomerService;

    @TestBean(/*methodName = ""*/) // use methodName to specify a custom factory method.
    CustomerService customerService;

    // by default method name is {beanName}TestOverride
    static CustomerService customerServiceTestOverride() {
        return new DummyCustomerService();
    }

    @Test
    public void test(ApplicationContext context) {
        assertThat(context.getBean("customerService"))
                .isSameAs(this.customerService)
                .isInstanceOf(DummyCustomerService.class);
    }

    @Test
    public void testCustomerService() {
        var customer = realCustomerService.findByEmail("foobar@example.com");
        assertThat(customer.firstName()).isEqualTo("foo");
        assertThat(customer.lastName()).isEqualTo("bar");
        assertThat(realCustomerService.findAll().size()).isEqualTo(2);

        // test bean
        var testCustomer = customerService.findByEmail("dummy@example.com");
        assertThat(testCustomer.firstName()).isEqualTo("dummy first");
        assertThat(testCustomer.lastName()).isEqualTo("dummy last");
        assertThat(customerService.findAll().size()).isEqualTo(0);
    }
}