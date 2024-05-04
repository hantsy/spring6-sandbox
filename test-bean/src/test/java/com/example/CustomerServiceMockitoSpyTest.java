package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = Config.class)
class CustomerServiceMockitoSpyTest {

    @MockitoSpyBean
    CustomerService customerService;

    // see: https://github.com/spring-projects/spring-framework/issues/32761
//    @MockitoSpyBean
//    CustomerService customerServiceSpy;

    @Test
    public void testCustomerService() {
        when(customerService.findByEmail("dummy@example.com"))
                .thenReturn(
                        new Customer("dummy first", "dummy last", "dummy@example.com")
                );

        // test bean
        var testCustomer = customerService.findByEmail("dummy@example.com");
        assertThat(testCustomer.firstName()).isEqualTo("dummy first");
        assertThat(testCustomer.lastName()).isEqualTo("dummy last");
        assertThat(customerService.findAll().size()).isEqualTo(2);

        verify(customerService, times(1)).findByEmail(anyString());
        verify(customerService, times(1)).findAll();
        verifyNoMoreInteractions(customerService);
    }
}