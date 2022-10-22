package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity()
@Table(name = "comments")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends AuditableEntity {

    @Column(name = "content")
    private String content;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "post_id"))
    private PostId postId;

}