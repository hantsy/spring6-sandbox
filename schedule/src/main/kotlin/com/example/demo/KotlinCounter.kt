package com.example.demo

import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicInteger

@Component
class KotlinCounter {
    companion object {
        val log = LoggerFactory.getLogger(KotlinCounter::class.java)
    }

    private val count = AtomicInteger(0)

    @Scheduled(fixedDelay = 5)
    suspend fun scheduled(): Void {
        return Mono.defer { Mono.just(count.incrementAndGet()) }
            .doOnNext { item: Int? -> log.debug("current count:{}", item) }
            .then()
            .awaitSingle()
    }

    suspend fun getInvocationCount(): Int {
        return count.get()
    }
}