package com.example.demo.it;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@Slf4j
public class IntegrationTestsWithRestAssured {

    @BeforeEach
    public void setup() {
        var baseUrl = "http://localhost:8080";
        if (System.getenv().containsKey("BASE_API_URL")) {
            baseUrl = System.getenv("BASE_API_URL");
        }
        log.debug("baseUrl is: {}", baseUrl);
        RestAssured.baseURI = baseUrl;
    }

    @AfterEach
    public void teardown() {
    }

    @Test
    public void getAllPostsWillBeOk() throws Exception {
        //@formatter:off
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/posts")
        .then()
                .statusCode(HttpStatus.SC_OK);
        //@formatter:on
    }

}
