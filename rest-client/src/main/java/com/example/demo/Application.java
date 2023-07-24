package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan
@Configuration
@Slf4j
public class Application {
    public static void main(String[] args) {
        var applicationContext = new AnnotationConfigApplicationContext(Application.class);
        var postClient = applicationContext.getBean(PostClient.class);

        postClientExample(postClient);
    }

    static void postClientExample(PostClient client) {
        client.allPosts().forEach(
                data -> log.debug("post: {}", data)
        );

        client.save(new Post(null, "test", "test content", Status.DRAFT, null));


    }
}
