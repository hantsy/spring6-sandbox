package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.ext.ContextResolver;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationWithJaxrsClientIT {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName(" GET '/' should return status 200")
    void testWithJerseyClient() {
        var client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        client.register(new ObjectMapperContextResolver(objectMapper), ContextResolver.class);
        var webTarget = client.target("http://localhost:8080/posts");
        var resGetAll = webTarget.request().accept("application/json").get();
        assertEquals(200, resGetAll.getStatus());
        var posts = resGetAll.readEntity(Post[].class);
        assertThat(posts.length).isEqualTo(1);
        assertThat(posts[0].getTitle()).contains("Tomcat");
    }

    private static final class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

        private final ObjectMapper objectMapper;

        private ObjectMapperContextResolver(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return this.objectMapper;
        }

    }

}
