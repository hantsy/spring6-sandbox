package com.example;

import java.util.List;

public interface CustomerService {
    Customer findByEmail(String email);

    List<Customer> findAll();
}


