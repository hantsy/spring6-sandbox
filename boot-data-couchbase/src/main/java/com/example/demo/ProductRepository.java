package com.example.demo;

import org.springframework.data.couchbase.repository.CouchbaseRepository;

public interface ProductRepository extends CouchbaseRepository<Product, String> {
}
