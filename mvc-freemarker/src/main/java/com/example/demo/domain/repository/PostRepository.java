package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.PostSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends ListPagingAndSortingRepository<Post, UUID>, ListCrudRepository<Post, UUID> {
    List<PostSummary> findBy();

    Page<PostSummary> findByTitleContains(String title, Pageable pageable);
}
