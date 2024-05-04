package com.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringJUnitConfig(classes = Config.class)
class CustomerServiceMockitoTest {

    @MockitoBean
    CustomerService customerServiceMock;

    @Test
    public void testCustomerService() {
        when(customerServiceMock.findByEmail("dummy@example.com"))
                .thenReturn(
                        new Customer("dummy first", "dummy last", "dummy@example.com")
                );
        when(customerServiceMock.findAll()).thenReturn(Collections.emptyList());

        // test bean
        var testCustomer = customerServiceMock.findByEmail("dummy@example.com");
        assertThat(testCustomer.firstName()).isEqualTo("dummy first");
        assertThat(testCustomer.lastName()).isEqualTo("dummy last");
        assertThat(customerServiceMock.findAll().size()).isEqualTo(0);

        verify(customerServiceMock, times(1)).findByEmail(anyString());
        verify(customerServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(customerServiceMock);
    }
}