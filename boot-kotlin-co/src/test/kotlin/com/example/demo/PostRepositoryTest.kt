package com.example.demo

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import org.springframework.transaction.reactive.transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
@DataR2dbcTest
@Testcontainers
class PostRepositoryTest {
    companion object {
        private val log = LoggerFactory.getLogger(PostRepositoryTest::class.java)

        @Container
        private val postgreSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:12")
            .withCopyToContainer(
                MountableFile.forClasspathResource("init.sql"),
                "/docker-entrypoint-initdb.d/init.sql"
            )

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                "r2dbc:postgresql://${postgreSQLContainer.host}:${postgreSQLContainer.firstMappedPort}/${postgreSQLContainer.databaseName}"
            }
            registry.add("spring.r2dbc.username") { postgreSQLContainer.username }
            registry.add("spring.r2dbc.password") { postgreSQLContainer.password }
        }
    }

    @Autowired
    lateinit var dbclient: DatabaseClient

    @Autowired
    lateinit var template: R2dbcEntityTemplate

    @Autowired
    lateinit var posts: PostRepository

    @Autowired
    lateinit var txOperator: TransactionalOperator

    @BeforeEach
    fun setup() = runTest {
        val deleted = template.delete(Post::class.java).all().awaitSingle()
        log.debug("clean posts list before tests: $deleted")
    }

    @Test
    fun `DatabaseClient should be existed`() {
        dbclient shouldNotBe null
    }

    @Test
    fun `R2dbcEntityTemplate should be existed`() {
        template shouldNotBe null
    }

    @Test
    fun `PostRepository bean should be existed`() {
        posts shouldNotBe null
    }

    fun `Flow transactional extension`() = runTest {
        flowOf("test 1", "test 2", "test 3")
            .onEach { posts.save(Post(title = it, content = "content of $it")) }
            .transactional(txOperator, newSingleThreadContext("MyContext"))
            .collect { log.debug("saved post: $it") }

        val allPosts = posts.findAll().toList()
        allPosts.size shouldBe 3
    }

    fun `transaction operator executeAndAwait`() = runTest {
        val post = txOperator.executeAndAwait { tx: ReactiveTransaction ->
            posts.save(Post(title = "foobar", content = "content of foobar"))
        }

        post.id shouldNotBe null
    }
}