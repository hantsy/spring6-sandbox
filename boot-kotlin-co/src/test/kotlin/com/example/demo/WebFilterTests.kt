package com.example.demo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@OptIn(ExperimentalCoroutinesApi::class)
@WebFluxTest(controllers = [HelloController::class])
class WebFilterTests {
    companion object {
        private val log = LoggerFactory.getLogger(WebFilterTests::class.java)
    }

    @TestConfiguration
    @Import(value = [SecurityWebFilter::class])
    class TestConfig

    @Autowired
    private lateinit var client: WebTestClient

    @BeforeEach
    fun beforeEach() {
        log.debug("calling before each...")
    }

    @Test
    fun `authentication failed with no user params`() = runTest{
        client.get().uri("/hello")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `authentication passed with user params`() =runTest {
        client.get().uri("/hello?user=Hantsy")
            .exchange()
            .expectStatus().isOk
    }
}