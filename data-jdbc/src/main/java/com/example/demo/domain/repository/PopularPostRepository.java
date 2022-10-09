package com.example.demo.domain.repository;

import com.example.demo.domain.model.RecordPost;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PopularPostRepository extends CrudRepository<RecordPost, UUID> {
}
