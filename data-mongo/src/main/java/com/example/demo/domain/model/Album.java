package com.example.demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document("albums")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album implements Persistable<UUID> {

    @Id
    UUID id;
    String name;

    String coverImage;

    @Field("post_slug")
    @DocumentReference(lookup = "{ 'slug' : ?#{#target} }")
    private Post story;

    @Builder.Default
    List<String> photos = new ArrayList<>();

    public static Album of(String name) {
        return Album.builder().name(name).build();
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }
}
