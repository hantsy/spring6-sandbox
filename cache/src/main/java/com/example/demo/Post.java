package com.example.demo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("posts")
@Setter
@Getter
@ToString
public class Post {

    @Id
    UUID id;
    String title;
    String content;

    public static Post of(String title, String content) {
        var data = new Post();
        data.setTitle(title);
        data.setContent(content);

        return data;
    }

}
