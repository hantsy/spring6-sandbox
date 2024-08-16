package com.example.demo;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostRepository postRepository;

    @GetMapping
    public String index() {
        return "posts/index";
    }

    // add post
    @PostMapping("/add")
    public String add() {
        return "posts/add";
    }

    // get post by id
    @GetMapping("/{id}")
    public String show() {
        return "posts/show";
    }

    // remove post
    @DeleteMapping("/{id}")
    public String remove() {
        return "posts/remove";
    }

    // add comment to post
    @PostMapping("/{id}/comments")
    public String addComment() {
        return "posts/add-comment";
    }

    // remove comment
    @DeleteMapping("/{id}/comments/{commentIdx}")
    public String removeComment() {
        return "posts/remove-comment";
    }
}
