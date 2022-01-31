package com.example.demo.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewPostModel {
    @NotEmpty
    @Size(min = 5)
    private String title;

    @NotEmpty
    @Size(min = 10)
    private String content;
}
