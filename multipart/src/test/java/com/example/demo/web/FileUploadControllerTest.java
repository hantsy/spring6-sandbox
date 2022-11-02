package com.example.demo.web;

import com.example.demo.Jackson2ObjectMapperConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.FormPartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

/**
 * @author hantsy
 */
@SpringJUnitConfig(classes = {WebConfig.class, Jackson2ObjectMapperConfig.class, FileUploadControllerTest.TestConfig.class})
@ActiveProfiles("mock")
public class FileUploadControllerTest {

    @Autowired
    FileUploadController fileUploadController;

    WebTestClient client;

    @BeforeEach
    public void setup() {
        this.client = WebTestClient
                .bindToController(fileUploadController)
                .configureClient().codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().enableLoggingRequestDetails(true))
                .build();
    }

    @Test
    public void testHandleFileUploadForm() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("name", "test");
        multipartBodyBuilder.part("file", new ClassPathResource("spring.png"), MediaType.IMAGE_PNG);
        var multipartBody = multipartBodyBuilder.build();
        this.client
                .post().uri("/form")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("test")
                .jsonPath("$.filename").isEqualTo("spring.png");
    }

    @Test
    public void testHandleRequestParts() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("name", "test");
        multipartBodyBuilder.part("file", new ClassPathResource("spring.png"), MediaType.IMAGE_PNG);
        var multipartBody = multipartBodyBuilder.build();
        this.client
                .post().uri("/requestparts")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("test")
                .jsonPath("$.filename").isEqualTo("spring.png");
    }

    @Test
    public void testHandleMultivalues() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("name", "test");
        multipartBodyBuilder.part("file", new ClassPathResource("spring.png"), MediaType.IMAGE_PNG);
        var multipartBody = multipartBodyBuilder.build();
        this.client
                .post().uri("/multivalues")
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.size()").isEqualTo(2);
    }

    @Test
    public void testHandlePartEvents() {
        this.client
                .post().uri("/partevents")
                .contentType(MULTIPART_FORM_DATA)
                .body(
                        Flux.concat(
                                FormPartEvent.create("name", "test"),
                                FilePartEvent.create("file", new ClassPathResource("spring.png"))
                        ),
                        PartEvent.class
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class).hasSize(2);
    }


    @Configuration
    @ComponentScan
    static class TestConfig {
    }

}
