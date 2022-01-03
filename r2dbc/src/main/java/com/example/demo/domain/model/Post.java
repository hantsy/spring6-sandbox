package com.example.demo.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author hantsy
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private UUID id;

    private String title;

    private String content;

    @Builder.Default
    private Status status = Status.DRAFT;

    private LocalDateTime createdAt;

}
