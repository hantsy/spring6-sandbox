package com.example.demo

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
@WebFluxTest(controllers = [PostController::class])
class PostControllerTest {

    @MockkBean
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var client: WebTestClient

    @Test
    fun `get posts by title with invalid title`() = runTest {
        coEvery { postRepository.findByTitleContains(any()) } returns
                flowOf(
                    Post(
                        id = 1,
                        title = "test title",
                        content = "test content",
                        createdAt = LocalDateTime.now()
                    )
                )


        client.get().uri("/byTitle?title=")
            .exchange()
            .expectStatus().isBadRequest

        coVerify(exactly = 0) { postRepository.findByTitleContains(any()) }
    }

    @Test
    fun `create post with invalid request body`() = runTest {
        coEvery { postRepository.save(any()) } returns
                Post(
                    id = 1,
                    title = "test title",
                    content = "test content",
                    createdAt = LocalDateTime.now()
                )

        val body = CreatePostCommand("test", "test content")

        client.post().uri("/")
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest

        coVerify(exactly = 0) { postRepository.save(any()) }
    }

    @Test
    fun `get post that not found`() = runTest {
        val id =1L
        coEvery { postRepository.findById(any()) } throws PostNotFoundException(id)

        val body = CreatePostCommand("test", "test content")

        client.get().uri("/$id")
            .exchange()
            .expectStatus().isNotFound

        coVerify(exactly = 1) { postRepository.findById(any()) }
    }
}
