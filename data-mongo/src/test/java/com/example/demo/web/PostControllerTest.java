package com.example.demo.web;


import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringJUnitWebConfig(classes = {Jackson2ObjectMapperConfig.class, WebConfig.class, TestDataConfig.class})
@ActiveProfiles("test")
public class PostControllerTest {

    @Autowired
    PostController ctrl;
    // WebApplicationContext ctx;

    @Autowired
    RestExceptionHandler exceptionHandler;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository posts;

    MockMvc rest;

    @BeforeEach
    public void setup() {
        this.rest = standaloneSetup(ctrl).setControllerAdvice(exceptionHandler)
                // webAppContextSetup(ctx)
                .addDispatcherServletCustomizer(c -> c.setEnableLoggingRequestDetails(true))
                .build();
    }

    @AfterEach
    public void teardown() {
        reset(this.posts);
    }

    @Test
    public void tetGetAllPosts() throws Exception {
        when(this.posts.findAll(isA(Predicate.class), isA(Pageable.class)))
                .thenReturn(new PageImpl<Post>(
                                List.of(
                                        Post.builder().title("test").content("content of test1").build(),
                                        Post.builder().title("test2").content("content of test2").build()
                                )
                        )
                );

        this.rest.perform(get("/posts").accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(), jsonPath("$.count", equalTo(2)));

        verify(this.posts, times(1)).findAll(isA(Predicate.class), isA(Pageable.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testGetPostById() throws Exception {
        when(this.posts.findById(anyString()))
                .thenReturn(Optional.of(Post.builder().title("test").content("content of test").build()));

        var id = ObjectId.get().toHexString();
        this.rest.perform(get("/posts/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title", is("test")),
                        jsonPath("$.content", is("content of test"))
                );

        verify(this.posts, times(1)).findById(id);
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testGetPostById_nonExisting() throws Exception {
        when(this.posts.findById(any()))
                .thenReturn(Optional.ofNullable(null));

        var id = ObjectId.get().toHexString();
        this.rest.perform(get("/posts/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(this.posts, times(1)).findById(id);
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testCreatePost() throws Exception {
        var id = ObjectId.get().toHexString();
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("test").content("content of test").build());

        var data = new CreatePostCommand("test post", "content of test");
        this.rest.perform(post("/posts").content(objectMapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/posts/" + id));

        verify(this.posts, times(1)).save(any(Post.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    @Disabled // see: https://github.com/spring-projects/spring-framework/issues/27868
    public void testCreatePost_validationFailed() throws Exception {
        var id = ObjectId.get().toHexString();
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("test").build());

        var data = new CreatePostCommand("a", "a");
        this.rest.perform(post("/posts").content(objectMapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", is("validation_failed")));

        verify(this.posts, times(0)).save(any(Post.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testUpdatePost() throws Exception {
        var id = ObjectId.get().toHexString();
        when(this.posts.findById(anyString()))
                .thenReturn(Optional.of(Post.builder().id(id).title("test").content("content of test").build()));
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("updated test").content("updated content of test").build());

        var data = new UpdatePostCommand("updated test", "updated content of test", Status.PUBLISHED);
        this.rest.perform(put("/posts/{id}", id).content(objectMapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(this.posts, times(1)).findById(id);
        verify(this.posts, times(1)).save(any(Post.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testUpdatePost_nonExisting() throws Exception {
        var id = ObjectId.get().toHexString();
        when(this.posts.findById(anyString()))
                .thenReturn(Optional.ofNullable(null));
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("updated test").content("updated content of test").build());

        var data = new UpdatePostCommand("updated test", "updated content of test", Status.PUBLISHED);
        this.rest.perform(put("/posts/{id}", id).content(objectMapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(this.posts, times(1)).findById(id);
        verify(this.posts, times(0)).save(any(Post.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testDeletePostById() throws Exception {
        doNothing().when(this.posts).deleteById(any(String.class));

        var id = ObjectId.get().toHexString();
        this.rest.perform(delete("/posts/{id}", id))
                .andExpect(status().isNoContent());

        verify(this.posts, times(1)).deleteById(id);
        verifyNoMoreInteractions(this.posts);
    }

//    @Test
//    public void testDeletePostById_nonExisting() throws Exception {
//        doThrow(EmptyResultDataAccessException.class).when(this.posts).deleteById(any(UUID.class));
//
//        var id = UUID.randomUUID();
//        this.rest.perform(delete("/posts/{id}", id))
//                .andExpect(status().isNotFound());
//
//        verify(this.posts, times(1)).deleteById(id);
//        verifyNoMoreInteractions(this.posts);
//    }

}
