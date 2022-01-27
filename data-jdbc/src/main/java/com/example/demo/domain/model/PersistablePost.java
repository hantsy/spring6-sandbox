package com.example.demo.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("persistable_posts")
@Setter
@Getter
@ToString
public class PersistablePost implements Persistable<UUID> {

    @Id
    UUID id;
    String title;
    String content;
    @Version
    Long version;

    @Override
    public boolean isNew() {
        return getId() == null;
    }
}
