package com.example.demo.domain.repository;

import com.example.demo.domain.model.PopularPost;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PopularPostRepository extends CrudRepository<PopularPost, UUID> {
}
