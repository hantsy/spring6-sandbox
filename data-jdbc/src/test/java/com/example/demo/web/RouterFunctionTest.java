package com.example.demo.web;

import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.model.Post;
import com.example.demo.domain.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author hantsy
 */
@SpringJUnitWebConfig(classes = {WebConfig.class, Jackson2ObjectMapperConfig.class, TestDataConfig.class})
public class RouterFunctionTest {

    @Autowired
    WebApplicationContext ctx;

    MockMvc rest;

    @Autowired
    PostRepository posts;

    @BeforeEach
    public void setup() {
        this.rest = webAppContextSetup(ctx)
                .addDispatcherServletCustomizer(s -> s.setEnableLoggingRequestDetails(true))
                .build();
    }

    @AfterEach
    public void teardown() {
        reset(this.posts);
    }

    @Test
    public void getGetAllPosts() throws Exception {
        when(this.posts.findAll())
                .thenReturn(List.of(
                                Post.builder().title("test").content("content of test1").build(),
                                Post.builder().title("test2").content("content of test2").build()
                        )
                );

        this.rest.perform(get("/posts").accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()", equalTo(2))
                );

        verify(this.posts, times(1)).findAll();
        verifyNoMoreInteractions(this.posts);
    }


}
