package com.pulserival.gamification

import com.pulserival.activity.dto.LogActivityCommand
import com.pulserival.activity.entity.ActivityLog
import com.pulserival.activity.entity.ActivityType
import com.pulserival.activity.repository.ActivityLogRepository
import com.pulserival.activity.service.ActivityLogService
import com.pulserival.gamification.service.LeaderboardService
import com.pulserival.identity.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.UUID
import org.awaitility.Awaitility.await
import java.util.concurrent.TimeUnit

@SpringBootTest
@Testcontainers
class EventWiringTest {

    // Reuse Redis setup
    class RedisContainer(imageName: String) : GenericContainer<RedisContainer>(DockerImageName.parse(imageName))

    companion object {
        @Container
        val redis: RedisContainer = RedisContainer("redis:7.2-alpine").withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }
        }
    }

    @Autowired
    private lateinit var activityLogService: ActivityLogService

    @Autowired
    private lateinit var leaderboardService: LeaderboardService

    @MockitoBean
    private lateinit var activityLogRepository: ActivityLogRepository

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @Test
    fun `when activity is logged, leaderboard should be updated via event`() {
        // Given
        val userId = UUID.randomUUID()
        val command = LogActivityCommand(
            userId = userId,
            type = ActivityType.STEPS,
            value = 500
        )
        
        // Mock SQL interactions
        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(activityLogRepository.save(any(ActivityLog::class.java))).thenAnswer { invocation ->
            val log = invocation.getArgument(0) as ActivityLog
            // Simulate DB saving by returning the object with an ID
            ActivityLog(
                userId = log.userId,
                type = log.type,
                value = log.value,
                occurredAt = log.occurredAt,
                rawData = log.rawData
            ) // In a real mock we might set ID, but not strictly needed here
        }

        // When
        activityLogService.logActivity(command)

        // Then
        // Wait up to 2 seconds for the async listener to update Redis
        await().atMost(2, TimeUnit.SECONDS).untilAsserted {
            val topUsers = leaderboardService.getGlobalLeaderboard(20)
            val myUserEntry = topUsers.find { it.userId == userId.toString() }
            
            if (myUserEntry == null) {
                // This exception triggers a retry in Awaitility
                throw AssertionError("User $userId not found yet in leaderboard. Current: $topUsers")
            }

            assertEquals(500.0, myUserEntry.score, 0.01)
        }
    }
}
