package com.example.demo.domain.model;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table("posts")
@Setter
@Getter
@Builder()
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    UUID id;
    String title;
    String content;

    @Builder.Default
    Status status = Status.DRAFT;

    @Builder.Default
    private Set<Label> labels = new HashSet<>();

    private AggregateReference<User, UUID> moderator;

    @CreatedDate
    @Column("created_at")
    LocalDateTime createdAt;

    @CreatedBy
    @Column("created_by")
    String createdBy;

    public void addLabel(String aLabel) {
        var l = Label.of(aLabel);
        l.setPost(this);
        this.labels.add(l);
    }

}
