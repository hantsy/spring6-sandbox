package com.example.demo

import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(classes = [SecurityWebFilterTest.TestConfig::class])
class SecurityWebFilterTest {
    companion object {
        private val log = LoggerFactory.getLogger(SecurityWebFilterTest::class.java)
    }

    @TestConfiguration
    @Import(value = [SecurityWebFilter::class])
    class TestConfig


    lateinit var client: WebTestClient

    @Autowired
    lateinit var context: ApplicationContext

    @BeforeEach
    fun beforeEach() {
        log.debug("calling before each...")
        client = WebTestClient.bindToApplicationContext(context)
            .build()
    }

    @Test
    fun `authentication failed with no user params`() {
        client.get().uri("/")
            .exchange()
            .expectStatus().isUnauthorized
    }
}