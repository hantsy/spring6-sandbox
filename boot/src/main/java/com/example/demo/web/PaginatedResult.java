package com.example.demo.web;

import java.util.List;

public record PaginatedResult<T>(List<T> data, Long count ) {

    public static <E> PaginatedResult<E> of(List<E> content, Long count) {
        return new PaginatedResult<>(content, count);
    }
}
