package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = Config.class)
class CustomerServiceMockitoSpyTest {
    // see: https://github.com/spring-projects/spring-framework/issues/32761
//    @MockitoSpyBean
//    CustomerService customerServiceSpy;

    // have to specify the bean name if variable name is not matched the bean name
    @MockitoSpyBean(name = "customerService")
    CustomerService customerServiceSpy;

    @Test
    public void testCustomerService() {
        when(customerServiceSpy.findByEmail("dummy@example.com"))
                .thenReturn(
                        new Customer("dummy first", "dummy last", "dummy@example.com")
                );

        // test bean
        var testCustomer = customerServiceSpy.findByEmail("dummy@example.com");
        assertThat(testCustomer.firstName()).isEqualTo("dummy first");
        assertThat(testCustomer.lastName()).isEqualTo("dummy last");
        assertThat(customerServiceSpy.findAll().size()).isEqualTo(2);

        verify(customerServiceSpy, times(1)).findByEmail(anyString());
        verify(customerServiceSpy, times(1)).findAll();
        verifyNoMoreInteractions(customerServiceSpy);
    }
}