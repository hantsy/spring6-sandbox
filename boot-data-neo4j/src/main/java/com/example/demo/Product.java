package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.math.BigDecimal;

@Node("products")
public record Product(@Id @GeneratedValue(UUIDStringGenerator.class) String id, String name, BigDecimal price) {
}
