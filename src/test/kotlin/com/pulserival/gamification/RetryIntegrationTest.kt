package com.pulserival.gamification

import com.pulserival.gamification.service.LeaderboardService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.anyDouble
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.retry.annotation.EnableRetry
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.UUID

@SpringBootTest(classes = [RetryIntegrationTest.TestConfig::class, LeaderboardService::class])
class RetryIntegrationTest {

    @TestConfiguration
    @EnableRetry
    class TestConfig {
        // We need to define the bean manually or MockitoBean it in the test class.
        // MockitoBean in the test class works with SpringBootTest.
    }

    @Autowired
    private lateinit var leaderboardService: LeaderboardService

    @MockitoBean
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Test
    fun `should retry connection failures and eventually succeed`() {
        // Given
        val userId = UUID.randomUUID()
        val zSetOps = mock(ZSetOperations::class.java) as ZSetOperations<String, String>
        
        // Mock the fluent API chain
        `when`(redisTemplate.opsForZSet()).thenReturn(zSetOps)

        // Simulate failure on 1st attempt, success on 2nd
        `when`(zSetOps.incrementScore(anyString(), anyString(), anyDouble()))
            .thenThrow(RuntimeException("Redis Connection Failed"))
            .thenReturn(10.0)

        // When
        leaderboardService.updateScore(userId, 50.0)

        // Then
        // Verify it was called twice (1 fail + 1 success)
        verify(zSetOps, times(2)).incrementScore(anyString(), anyString(), anyDouble())
    }
    
    @Test
    fun `should recover after max attempts exhausted`() {
        // Given
        val userId = UUID.randomUUID()
        val zSetOps = mock(ZSetOperations::class.java) as ZSetOperations<String, String>
        
        `when`(redisTemplate.opsForZSet()).thenReturn(zSetOps)

        // Simulate failure on ALL 3 attempts
        `when`(zSetOps.incrementScore(anyString(), anyString(), anyDouble()))
            .thenThrow(RuntimeException("Redis Connection Failed"))

        // When
        // This should NOT throw an exception because @Recover handles it
        leaderboardService.updateScore(userId, 50.0)

        // Then
        // Verify it was called 3 times (Max attempts)
        verify(zSetOps, times(3)).incrementScore(anyString(), anyString(), anyDouble())
    }
}
