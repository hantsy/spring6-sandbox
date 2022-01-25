package com.example.demo.domain.repository;

import com.example.demo.domain.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class PostRepositoryImpl implements PostRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<Post> findPostByLabels(String... labels) {
        var criteria = Criteria.where("labels");
        return this.mongoTemplate.query(Post.class)
                .matching(criteria.in(labels))
                .all();
    }

}
