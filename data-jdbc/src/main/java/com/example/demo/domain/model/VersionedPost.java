package com.example.demo.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("versioned_posts")
@Setter
@Getter
@ToString
public class VersionedPost {

    @Id
    UUID id;
    String title;
    String content;
    @Version
    Long version;
}
