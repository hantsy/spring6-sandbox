package com.example.demo;

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

    enum Status {
        DRAFT, PENDING_MODERATION, PUBLISHED;
    }

}
