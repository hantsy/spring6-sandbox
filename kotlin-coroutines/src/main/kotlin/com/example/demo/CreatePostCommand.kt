package com.example.demo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreatePostCommand(
    @field:NotBlank
    @field:Size(min = 5)
    val title: String,
    @field:NotBlank
    val content: String
)