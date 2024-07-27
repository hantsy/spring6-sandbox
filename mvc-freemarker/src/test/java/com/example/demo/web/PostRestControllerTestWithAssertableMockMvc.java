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
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author hantsy
 */
@SpringJUnitWebConfig(classes = {WebConfig.class, Jackson2ObjectMapperConfig.class, TestDataConfig.class})
public class PostRestControllerTestWithAssertableMockMvc {

    @Autowired
    WebApplicationContext ctx;

    MockMvcTester mvc;

    @Autowired
    PostRepository posts;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcTester.from(ctx, builder ->
                builder.addDispatcherServletCustomizer(dispatcherServlet ->
                                dispatcherServlet.setEnableLoggingRequestDetails(true)
                        )
                        .build()
        );
    }

    @AfterEach
    public void teardown() {
        reset(this.posts);
    }

    @Test
    public void getGetPostById() {
        when(this.posts.findById(any(UUID.class)))
                .thenReturn(
                        Optional.of(Post.of("test", "content of test1"))
                );

        assertThat(this.mvc.perform(get("/api/posts/{id}", UUID.randomUUID()).accept(MediaType.APPLICATION_JSON)))
                .hasStatusOk()
                .bodyJson().hasPath("$.title");

        verify(this.posts, times(1)).findById(any(UUID.class));
        verifyNoMoreInteractions(this.posts);
    }

    @Test
    public void getGetPostByIdNotFoundException() {
        when(this.posts.findById(any(UUID.class)))
                .thenReturn(
                        Optional.ofNullable(null)
                );

        assertThat(this.mvc.perform(get("/api/posts/{id}", UUID.randomUUID()).accept(MediaType.APPLICATION_JSON)))
                // it throws a ServletException
                .hasFailed().failure().hasCauseInstanceOf(PostNotFoundException.class);

        verify(this.posts, times(1)).findById(any(UUID.class));
        verifyNoMoreInteractions(this.posts);
    }


    @Test
    public void getGetAllPosts() throws Exception {
        when(this.posts.findAll())
                .thenReturn(List.of(
                                Post.of("test", "content of test1"),
                                Post.of("test2", "content of test2")
                        )
                );

        assertThat(this.mvc.perform(get("/api/posts").accept(MediaType.APPLICATION_JSON)))
                .hasStatusOk()
                .bodyJson().hasPathSatisfying("$.size()", v -> v.assertThat().isEqualTo(2));
        //       .has(new Condition<MvcTestResult>());

        verify(this.posts, times(1)).findAll();
        verifyNoMoreInteractions(this.posts);
    }

}
