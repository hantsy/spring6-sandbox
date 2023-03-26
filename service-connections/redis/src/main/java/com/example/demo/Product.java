package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

@RedisHash("products")
public record Product(@Id String id, String name, BigDecimal price) {
}
