package com.example.demo.domain.repository;

import com.example.demo.domain.model.QPost;
import com.example.demo.domain.model.Status;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.util.StringUtils;

public class Predicates {

    public static Predicate findByKeyword(String q, Status s) {
        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(q)) {
            BooleanBuilder keywordPredicate = new BooleanBuilder(post.title.like("%" + q + "%"));
            keywordPredicate.or(post.content.like("%" + q + "%"));
            builder.and(keywordPredicate);
        }

        if (s != null) {
            var statusPredicate = post.status.eq(s);
            builder.and(statusPredicate);
        }

        return builder;//.getValue();
    }
}
