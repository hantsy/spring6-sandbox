package com.example.demo.web;

import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;


@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostRepository posts;

    @GetMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
    public String getAll(Model model) {
        var data = this.posts.findBy();
        model.addAttribute("posts", data);
        return "posts";
    }

    @GetMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE)
    public String newPost(Model model) {
        model.addAttribute("post", new NewPostModel());
        return "new";
    }

    @PostMapping(value = "")
    public String create(@ModelAttribute("post") @Valid NewPostModel post,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("flashMessage", AlertMessage.danger("Invalid input data!"));
            return "new";
        }

        var data = Post.of(post.getTitle(), post.getContent());
        this.posts.save(data);
        redirectAttributes.addFlashAttribute("flashMessage", AlertMessage.success("Post is saved successfully!"));
        return "redirect:/posts";
    }

    @GetMapping(value = "/{id}/edit", produces = MediaType.TEXT_HTML_VALUE)
    public String editPost(@PathVariable("id") UUID id, Model model) {
        return this.posts.findById(id)
                .map(p -> {
                    model.addAttribute("post", new EditPostModel(p.getId(), p.getTitle(), p.getContent()));
                    return "edit";
                })
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    @PutMapping(value = "{id}")
    public String update(@PathVariable UUID id, @ModelAttribute("post") EditPostModel dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("flashMessage", AlertMessage.danger("Invalid input data!"));
            return "edit";
        }

        return posts.findById(id)
                .map(p -> {
                    p.setTitle(dto.getTitle());
                    p.setContent(dto.getContent());
                    // p.setStatus(dto.status());
                    this.posts.save(p);
                    redirectAttributes.addFlashAttribute("flashMessage", AlertMessage.info("Post is updated successfully!"));
                    return "redirect:/posts";
                })
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    @GetMapping(value = "{id}")
    public String getById(@PathVariable UUID id, Model model) {
        return posts.findById(id)
                .map(p -> {
                    model.addAttribute("details", p);
                    return "details";
                })
                .orElseThrow(() -> new PostNotFoundException(id));
    }


    @DeleteMapping(value = "{id}")
    public String deleteById(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            posts.deleteById(id);
            redirectAttributes.addFlashAttribute("flashMessage", AlertMessage.info("Post is deleted successfully!"));
            return "redirect:/posts";
        } catch (EmptyResultDataAccessException e) {
            throw new PostNotFoundException(id);
        }
    }

    @ExceptionHandler(value = PostNotFoundException.class)
    public String notFound(PostNotFoundException ex, Model model) {
        model.addAttribute("ex", ex.getMessage());
        return "error";
    }

}

