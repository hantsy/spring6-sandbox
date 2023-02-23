package com.example.demo

import io.netty.handler.codec.http.HttpResponseStatus
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/")
@Validated
class PostController(private val postRepository: PostRepository) {

    @GetMapping("")
    fun findAll(): Flow<Post> = postRepository.findAll()

    @GetMapping("{id}")
    suspend fun findOne(@PathVariable id: Long): Post? =
        postRepository.findById(id) ?: throw PostNotFoundException(id)

    @GetMapping("byTitle")
    fun findByName(@RequestParam @NotBlank title: String): Flow<Post> =
        postRepository.findByTitleContains(title)

    @PostMapping("")
    suspend fun save(@RequestBody @Valid body: CreatePostCommand): Post =
        postRepository.save(Post(title = body.title, content = body.content))

    @ExceptionHandler(value = [WebExchangeBindException::class, ConstraintViolationException::class])
    fun handleBadRequest(ex: Exception, exchange: ServerWebExchange) : ResponseEntity<Any> {
        return status(BAD_REQUEST).body(ex.message)
    }

    @ExceptionHandler(value = [PostNotFoundException::class])
    fun handleNotFound(ex: Exception, exchange: ServerWebExchange) : ResponseEntity<Any> {
        return status(NOT_FOUND).body(ex.message)
    }

}