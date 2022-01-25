package com.example.demo.domain.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostCallback implements BeforeConvertCallback<Post> {
    
    @Override
    public Post onBeforeConvert(Post entity, String collection) {
        if (entity.getId() == null) { // only convert when being persisted.
            log.debug("post title: {}", entity.getTitle());
            var slug = entity.getTitle().trim()
                    .translateEscapes()
                    .toLowerCase()
                    .replaceAll("(?U)\\s+", " ")
                    .replace(" ", "-");
            entity.setSlug(slug);
            log.debug("converted to slug: {}", slug);
        }
        return entity;
    }
}
