package com.example.demo.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditPostModel {
    private UUID id;

    @NotEmpty
    @Size(min = 5)
    private String title;

    @NotEmpty
    @Size(min = 10)
    private String content;
}
