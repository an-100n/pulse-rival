package com.pulserival.gamification

import com.pulserival.gamification.service.LeaderboardService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.UUID

@SpringBootTest
@Testcontainers
class LeaderboardIntegrationTest {

    // Helper class to satisfy GenericContainer<SELF> recursive type
    class RedisContainer(imageName: String) : GenericContainer<RedisContainer>(DockerImageName.parse(imageName))

    companion object {
        @Container
        val redis = RedisContainer("redis:7.2-alpine").withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }
        }
    }

    @Autowired
    private lateinit var leaderboardService: LeaderboardService

    @Test
    fun `should update and retrieve leaderboard scores`() {
        // Given
        val user1 = UUID.randomUUID()
        val user2 = UUID.randomUUID()
        val user3 = UUID.randomUUID()

        // When
        // User 1 gets 100 points
        leaderboardService.updateScore(user1, 100.0)
        // User 2 gets 200 points
        leaderboardService.updateScore(user2, 200.0)
        // User 3 gets 50 points
        leaderboardService.updateScore(user3, 50.0)
        
        // User 1 gets another 50 points (Total 150)
        leaderboardService.updateScore(user1, 50.0)

        // Then
        val topUsers = leaderboardService.getGlobalLeaderboard(10)
        
        // Expected order: 
        // 1. user2 (200)
        // 2. user1 (150)
        // 3. user3 (50)
        
        assertEquals(3, topUsers.size, "Should have 3 users in the leaderboard")
        
        // Check Rank 1
        assertEquals(user2.toString(), topUsers[0].userId)
        assertEquals(200.0, topUsers[0].score, 0.01)
        assertEquals(1, topUsers[0].rank)
        
        // Check Rank 2
        assertEquals(user1.toString(), topUsers[1].userId)
        assertEquals(150.0, topUsers[1].score, 0.01)
        assertEquals(2, topUsers[1].rank)
        
        // Check Rank 3
        assertEquals(user3.toString(), topUsers[2].userId)
        assertEquals(50.0, topUsers[2].score, 0.01)
        assertEquals(3, topUsers[2].rank)
    }
}