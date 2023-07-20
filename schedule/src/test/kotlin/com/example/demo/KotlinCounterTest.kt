package com.example.demo

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig


@SpringJUnitConfig(value = [TestConfig::class])
class KotlinCounterTest {

    @Autowired
    lateinit var counter: KotlinCounter

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun givenSleepBy100ms_whenGetInvocationCount_thenIsGreaterThanZero() = runTest {
        assertThat(counter.getInvocationCount()).isGreaterThan(0)
    }

}