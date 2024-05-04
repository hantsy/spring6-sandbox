package com.example;

import java.util.List;

public class DefaultCustomerService implements CustomerService {
    @Override
    public Customer findByEmail(String email) {
        return new Customer("foo", "bar", "foobar@example.com");
    }

    @Override
    public List<Customer> findAll() {
        return List.of(
                new Customer("foo", "bar", "foobar@example.com"),
                new Customer("foo2", "bar2", "foobar2@example.com")
        );
    }
}
