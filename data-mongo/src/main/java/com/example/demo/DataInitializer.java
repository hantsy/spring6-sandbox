package com.example.demo;

import com.example.demo.domain.model.Album;
import com.example.demo.domain.model.Comment;
import com.example.demo.domain.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer {

    private final MongoTemplate template;

    @EventListener(value = ContextRefreshedEvent.class)
    public void init() throws Exception {
        log.info("start data initialization...");
        template.dropCollection(Post.class);
        template.dropCollection(Comment.class);
        template.dropCollection(Album.class);

        var post = Post.builder()
                .title("Spring Data Mongo")
                .content("content of Spring Data Mongo")
                .build();
        var saved = template.insert(post);

        var comment = Comment.of("test comment");
        var savedComment = template.insert(comment);

        var result = template.update(Post.class)
                .matching(where("id").is(saved.getId()))
                .apply(new Update().push("comments", savedComment)).all();
        log.debug("updated result: {}", result.getModifiedCount());

        var updated = template.findOne(query(where("id").is(saved.getId())), Post.class);
        log.debug("updated post: {}", updated);
    }
}