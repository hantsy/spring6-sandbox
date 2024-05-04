package com.example;

import java.util.List;

class DummyCustomerService implements CustomerService {
    @Override
    public Customer findByEmail(String email) {
        return new Customer("dummy first", "dummy last", "dummy@example.com");
    }

    @Override
    public List<Customer> findAll() {
        return List.of();
    }
}
