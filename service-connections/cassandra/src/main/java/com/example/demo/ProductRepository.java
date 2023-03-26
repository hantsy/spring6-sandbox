package com.example.demo;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ProductRepository extends CassandraRepository<Product, String> {
}
