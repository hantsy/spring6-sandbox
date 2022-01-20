package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity()
@Table(name = "posts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(value = AuditingEntityListener.class) //to fill the auditing fields.
public class Post implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @CollectionTable(name = "post_labels", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name="label")
    @ElementCollection()
    @Builder.Default
    private Set<String> labels = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

}