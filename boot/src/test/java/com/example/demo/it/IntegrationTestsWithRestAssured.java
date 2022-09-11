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
        RestAssured.port = 8080;
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
