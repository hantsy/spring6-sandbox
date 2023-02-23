package com.example.demo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface PostRepository : CoroutineCrudRepository<Post, Long>, CoroutineSortingRepository<Post, Long> {
    fun findByTitleContains(title: String): Flow<Post>
}