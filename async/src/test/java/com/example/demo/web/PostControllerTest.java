package com.example.demo.web;

import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import com.example.demo.service.PostCreated;
import com.example.demo.service.PostEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @author hantsy
 */
@SpringJUnitWebConfig(classes = {Jackson2ObjectMapperConfig.class, WebConfig.class, TestDataConfig.class})
@ActiveProfiles("test")
@Slf4j
public class PostControllerTest {

    @Autowired
    PostController ctrl;
    // WebApplicationContext ctx;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository posts;

    @Autowired
    PostEventPublisher eventPublisher;

    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = standaloneSetup(ctrl)
                // webAppContextSetup(ctx)
                .addDispatcherServletCustomizer(c -> c.setEnableLoggingRequestDetails(true))
                .build();
    }

    @AfterEach
    public void teardown() {
        reset(this.posts, this.eventPublisher);
    }

    @Test
    public void tetGetAllPosts() throws Exception {
        when(this.posts.readAllBy())
                .thenReturn(CompletableFuture.completedFuture(
                                List.of(
                                        Post.builder().title("test").content("content of test1").build(),
                                        Post.builder().title("test2").content("content of test2").build()
                                )
                        )
                );

        var mvcResult = mockMvc.perform(get("/posts").accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andExpect(status().isOk())
                .andReturn();

        log.debug("mvcResult.getRequest().isAsyncStarted(): {}", mvcResult.getRequest().isAsyncStarted());
        mvcResult.getAsyncResult(500L);

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()", equalTo(2))
                );

        verify(this.posts, times(1)).readAllBy();
        verifyNoMoreInteractions(this.posts);
    }
    
    @Test
    public void testCreatePost() throws Exception {
        var id = UUID.randomUUID();
        when(this.posts.save(any(Post.class)))
                .thenReturn(Post.builder().id(id).title("test").content("content of test").build());
        doNothing().when(this.eventPublisher).publishPostCreated(any(PostCreated.class));

        var data = new CreatePostCommand("test post", "content of test");
        this.mockMvc.perform(post("/posts").content(objectMapper.writeValueAsBytes(data)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/posts/" + id));

        verify(this.posts, times(1)).save(any(Post.class));
        verify(this.eventPublisher, times(1)).publishPostCreated(any(PostCreated.class));
        verifyNoMoreInteractions(this.posts);
        verifyNoMoreInteractions(this.eventPublisher);
    }
}
