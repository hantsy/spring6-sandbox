package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Post_;
import com.example.demo.domain.model.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Specifications {
    public static Specification<Post> findByKeyword(String q, Status status) {
        return (root, query, cb) -> {
            // compute the query where predicates
            List<Predicate> predicates = new ArrayList<>();
            // if keyword is provided
            if (StringUtils.hasText(q)) {
                predicates.add(
                        cb.or(
                                cb.like(root.get(Post_.title), "%" + q + "%"),
                                cb.like(root.get(Post_.content), "%" + q + "%")
                        )
                );
            }
            // if status is set
            if (status != null) {
                predicates.add(cb.equal(root.get(Post_.status), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
