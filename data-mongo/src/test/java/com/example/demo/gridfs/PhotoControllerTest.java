package com.example.demo.gridfs;

import com.example.demo.Jackson2ObjectMapperConfig;
import com.example.demo.domain.MongoConfig;
import com.example.demo.web.PhotoController;
import com.example.demo.web.WebConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringJUnitWebConfig(classes = {WebConfig.class, PhotoControllerTest.TestConfig.class})
public class PhotoControllerTest {

    @Configuration
    @Import({Jackson2ObjectMapperConfig.class, MongoConfig.class})
    @ComponentScan(basePackageClasses = {WebConfig.class})
    static class TestConfig {
    }

    @Autowired
    PhotoController ctrl;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc rest;

    @BeforeEach
    public void setup() {
        this.rest = standaloneSetup(ctrl)
                // webAppContextSetup(ctx)
                .addDispatcherServletCustomizer(c -> c.setEnableLoggingRequestDetails(true))
                .build();
    }

    @SneakyThrows
    @Test
    public void testUploadAndDownload() {

        // create a mockfile
        var mockFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "abc".getBytes(StandardCharsets.UTF_8));

        //upload
        var result = this.rest.perform(MockMvcRequestBuilders.multipart("/photos").file(mockFile))
                .andExpect(status().isOk())
                .andReturn();
        var resultMap = objectMapper.readValue(result.getResponse().getContentAsByteArray(), Map.class);
        var id = (String) resultMap.get("id");

        assertThat(id).isNotNull();

        // download file
        var downloadResult = this.rest.perform(MockMvcRequestBuilders.get("/photos/{id}", id).accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE))
                .andReturn();

        //compare the response content with the original file content.
        var fileContent = FileCopyUtils.copyToString(new InputStreamReader(new ByteArrayInputStream(downloadResult.getResponse().getContentAsByteArray())));
        assertThat(fileContent).isEqualTo("abc");

        // delete file
        this.rest.perform(MockMvcRequestBuilders.delete("/photos/{id}", id))
                .andExpect(status().isNoContent());

        // verify the file is deleted
        this.rest.perform(MockMvcRequestBuilders.get("/photos/{id}", id))
                .andExpect(status().isNotFound());
    }

}
