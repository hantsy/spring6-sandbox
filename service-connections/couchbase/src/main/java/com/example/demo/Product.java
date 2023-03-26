package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.math.BigDecimal;

@Document
public record Product(@Id @GeneratedValue(strategy = GenerationStrategy.UNIQUE) String id,
                      String name,
                      BigDecimal price) {
}
