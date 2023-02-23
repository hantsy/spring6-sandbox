package com.example.demo

import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DataInitializer(private val posts: PostRepository) {

    @EventListener(value = [ApplicationReadyEvent::class])
    fun init() {
        runBlocking {
            println(" print initial data...")
            posts.findAll()
        }
    }
}