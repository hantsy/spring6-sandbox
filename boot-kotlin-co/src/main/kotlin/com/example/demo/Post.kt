package com.example.demo

import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("posts")
data class Post(
    @Id val id: Long? = null,
    @field:NotBlank @Column("title") val title: String? = null,
    @Column("content") val content: String? = null,
    @Column("created_at") val createdAt: LocalDateTime? = null
)