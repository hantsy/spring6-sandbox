package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = Config.class)
class CustomerServiceTest {

    // the real bean is not injected.
    // see: https://github.com/spring-projects/spring-framework/issues/32759
//    @Autowired
//    CustomerService realCustomerService;

    // have to set `name` attribute when the variable name is not matched the bean name
    // see: https://github.com/spring-projects/spring-framework/issues/32760
    // the `methodName` is used to specify a custom factory method if it does not follow the convention.
    @TestBean(name="customerService"/*, methodName = ""*/)
    CustomerService testCustomerService;

    // by default method name is {beanName}TestOverride
    static CustomerService customerServiceTestOverride() {
        return new DummyCustomerService();
    }

    @Test
    public void test(ApplicationContext context) {
        assertThat(context.getBean("customerService"))
                .isSameAs(this.testCustomerService)
                .isInstanceOf(DummyCustomerService.class);
    }

    @Test
    public void testCustomerService() {
//        var customer = realCustomerService.findByEmail("foobar@example.com");
//        assertThat(customer.firstName()).isEqualTo("foo");
//        assertThat(customer.lastName()).isEqualTo("bar");
//        assertThat(realCustomerService.findAll().size()).isEqualTo(2);

        // test bean
        var testCustomer = testCustomerService.findByEmail("dummy@example.com");
        assertThat(testCustomer.firstName()).isEqualTo("dummy first");
        assertThat(testCustomer.lastName()).isEqualTo("dummy last");
        assertThat(testCustomerService.findAll().size()).isEqualTo(0);
    }
}