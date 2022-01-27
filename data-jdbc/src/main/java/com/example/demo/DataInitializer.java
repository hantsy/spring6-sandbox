package com.example.demo;

import com.example.demo.domain.model.Label;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("dev")
public class DataInitializer {

    private final JdbcAggregateTemplate template;
    private final TransactionTemplate tx;

    @EventListener(value = ContextRefreshedEvent.class)
    public void init() throws Exception {
        log.info("start data initialization...");
        tx.executeWithoutResult(status -> {
            this.template.deleteAll(Label.class);
            this.template.deleteAll(Post.class);
            this.template.deleteAll(User.class);

            var user = User.of("Hantsy", "hantsy@example.com");
            var savedUser = this.template.save(user);

            var post = Post.builder()
                    .title("Getting Started with Spring Data Jdbc")
                    .content("The content of Getting Started with Spring Data Jdbc")
                    .moderator(AggregateReference.to(savedUser.getId()))
                    .build();
            post.addLabel("Spring");
            post.addLabel("Spring Data Jdbc");

            var savedPost = this.template.save(post);

            var foundPost = this.template.findById(savedPost.getId(), Post.class);
            log.debug("found post by id: {}", foundPost);
        });
    }
}