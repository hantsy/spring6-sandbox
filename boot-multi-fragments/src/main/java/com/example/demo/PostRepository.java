package com.example.demo;


import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Collections.emptyList;

@Repository
public class PostRepository {
    private final static ConcurrentHashMap<Long, Post> store = new ConcurrentHashMap<>();
    private final static AtomicLong ID_GENERATOR = new AtomicLong(1);

    static {
        var id = ID_GENERATOR.getAndIncrement();
        store.put(
                id,
                new Post(
                        id,
                        "Post 1",
                        Author.of("Author 1"),
                        "The content of post 1",

                        LocalDateTime.now(),
                        List.of(new Comment("Test comment1", LocalDateTime.now()))
                )
        );

        id = ID_GENERATOR.getAndIncrement();
        store.put(
                id,
                new Post(
                        id,
                        "Post 2",
                        Author.of("Author 2"),
                        "The content of post 2",
                        LocalDateTime.now(),
                        emptyList()
                )
        );
    }

    public List<Post> all() {
        return List.copyOf(store.values());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }

    public Post findById(Long id) {
        return store.get(id);
    }

    public Post save(Post post) {
        var id = post.id();
        if (id == null) {
            id = ID_GENERATOR.getAndIncrement();
            var postWithId = post.withId(id);
            store.put(id, postWithId);
            return postWithId;
        } else {
            store.put(id, post);
            return post;
        }
    }

    public void addComment(Long postId, String comment) {
        var post = store.get(postId);
        post.comments().add(new Comment(comment, LocalDateTime.now()));
    }

    public void removeComment(Long postId, Integer commentIdx) {
        var post = store.get(postId);
        post.comments().remove(commentIdx.intValue());
    }
}
