package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity()
@Table(name = "posts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist // prepersist callback
    public void beforePersist() {
        this.setCreatedAt(LocalDateTime.now());
    }

}