package com.example.demo.web;

import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.model.PostSummary;
import com.example.demo.domain.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringJUnitWebConfig(classes = {WebConfig.class, Jackson2ObjectMapperConfig.class, TestDataConfig.class})
public class PostControllerTest {

    @Autowired
    WebApplicationContext ctx;

    MockMvc mockMvc;

    @Autowired
    PostRepository posts;

    @BeforeEach
    public void setup() {
        this.mockMvc = webAppContextSetup(ctx)
                .addDispatcherServletCustomizer(s -> s.setEnableLoggingRequestDetails(true))
                .build();
    }

    @AfterEach
    public void teardown() {
        reset(this.posts);
    }

    @Test
    public void testPostList() throws Exception {
        when(this.posts.findBy())
                .thenReturn(List.of(
                                new PostSummary(UUID.randomUUID(), "test", LocalDateTime.now()),
                                new PostSummary(UUID.randomUUID(), "test2", LocalDateTime.now())
                        )
                );

        this.mockMvc.perform(get("/posts"))
                .andExpectAll(
                        status().isOk(),
                        xpath("//table//tbody//tr").exists()
                );

        verify(this.posts, times(1)).findBy();
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void createPost() throws Exception {
        mockMvc.perform(post("/posts")
                        .param("title", "First post")
                        .param("content", "Description of my first post"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    public void createPostForm() throws Exception {
        mockMvc.perform(get("/posts/new"))
                .andExpect(xpath("//input[@name='title']").exists())
                .andExpect(xpath("//textarea[@name='content']").exists());
    }


}
