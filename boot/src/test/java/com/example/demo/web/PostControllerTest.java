package com.example.demo.web;


import com.example.demo.domain.model.Post;
import com.example.demo.domain.model.Status;
import com.example.demo.domain.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @author hantsy
 */
@WebMvcTest
public class PostControllerTest {

    @Autowired
    PostController ctrl;
    // WebApplicationContext ctx;
    @Autowired
    RestExceptionHandler exceptionHandler;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
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
    }

    @Test
    public void tetGetAllPosts() throws Exception {
        when(this.posts.findAll(isA(Specification.class), isA(Pageable.class)))
                .thenReturn(new PageImpl<Post>(
                                List.of(
                                        Post.builder().title("test").content("data of test1").build(),
                                        Post.builder().title("test2").content("data of test2").build()
                                )
                        )
                );

        this.rest.perform(get("/posts").accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(), jsonPath("$.count", equalTo(2)));

        verify(this.posts, times(1)).findAll(isA(Specification.class), isA(Pageable.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testGetPostById() throws Exception {
        when(this.posts.findById(any(UUID.class)))
                .thenReturn(Optional.of(Post.builder().title("test").content("data of test").build()));

        var id = UUID.randomUUID();
        this.rest.perform(get("/posts/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title", is("test")),
                        jsonPath("$.content", is("data of test"))
                );

        verify(this.posts, times(1)).findById(id);
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testGetPostById_nonExisting() throws Exception {
        when(this.posts.findById(any()))
                .thenReturn(Optional.ofNullable(null));

        var id = UUID.randomUUID();
        this.rest.perform(get("/posts/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(this.posts, times(1)).findById(id);
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testCreatePost() throws Exception {
        var id = UUID.randomUUID();
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("test").content("data of test").build());

        var data = new CreatePostCommand("test post", "data of test");
        this.rest.perform(post("/posts").content(objectMapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/posts/" + id));

        verify(this.posts, times(1)).save(any(Post.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    //@Disabled // see: https://github.com/spring-projects/spring-framework/issues/27868
    public void testCreatePost_validationFailed() throws Exception {
        var id = UUID.randomUUID();
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("test").build());

        var data = new CreatePostCommand("a", "a");
        this.rest.perform(post("/posts").content(objectMapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(this.posts, times(0)).save(any(Post.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testUpdatePost() throws Exception {
        var id = UUID.randomUUID();
        when(this.posts.findById(any(UUID.class)))
                .thenReturn(Optional.of(Post.builder().id(id).title("test").content("data of test").build()));
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("updated test").content("updated data of test").build());

        var data = new UpdatePostCommand("updated test", "updated data of test", Status.PUBLISHED);
        this.rest.perform(put("/posts/{id}", id).content(objectMapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(this.posts, times(1)).findById(id);
        verify(this.posts, times(1)).save(any(Post.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testUpdatePost_nonExisting() throws Exception {
        var id = UUID.randomUUID();
        when(this.posts.findById(any(UUID.class)))
                .thenReturn(Optional.ofNullable(null));
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("updated test").content("updated data of test").build());

        var data = new UpdatePostCommand("updated test", "updated data of test", Status.PUBLISHED);
        this.rest.perform(put("/posts/{id}", id).content(objectMapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(this.posts, times(1)).findById(id);
        verify(this.posts, times(0)).save(any(Post.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testDeletePostById() throws Exception {
        doNothing().when(this.posts).deleteById(any(UUID.class));

        var id = UUID.randomUUID();
        this.rest.perform(delete("/posts/{id}", id))
                .andExpect(status().isNoContent());

        verify(this.posts, times(1)).deleteById(id);
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void testDeletePostById_nonExisting() throws Exception {
        doThrow(EmptyResultDataAccessException.class).when(this.posts).deleteById(any(UUID.class));

        var id = UUID.randomUUID();
        this.rest.perform(delete("/posts/{id}", id))
                .andExpect(status().isNotFound());

        verify(this.posts, times(1)).deleteById(id);
        verifyNoMoreInteractions(this.posts);
    }

}
