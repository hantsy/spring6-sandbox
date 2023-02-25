package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.UUID;

public interface PostRepositoryCustom {
    List<Post> findPostByLabels(String... labels);

    // custom typesafe criteria query result with the affected result count.
    int customDeleteById(UUID id);

    int customDeleteAll();

    int updateStatus(UUID id, Status status);
}
