package com.pulserival.gamification

import com.pulserival.activity.dto.LogActivityCommand
import com.pulserival.activity.entity.ActivityLog
import com.pulserival.activity.entity.ActivityType
import com.pulserival.activity.repository.ActivityLogRepository
import com.pulserival.activity.service.ActivityLogService
import com.pulserival.gamification.service.LeaderboardService
import com.pulserival.identity.entity.User
import com.pulserival.identity.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.UUID
import org.awaitility.Awaitility.await
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import java.time.Instant

@SpringBootTest
@Testcontainers
class EventWiringTest {

    // Reuse Redis setup
    class RedisContainer(imageName: String) : GenericContainer<RedisContainer>(DockerImageName.parse(imageName))

    companion object {
        @Container
        val redis: RedisContainer = RedisContainer("redis:7.2-alpine").withExposedPorts(6379)

        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            // Redis
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }

            // Postgres
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Autowired
    private lateinit var activityLogService: ActivityLogService

    @Autowired
    private lateinit var leaderboardService: LeaderboardService

    @Autowired
    private lateinit var activityLogRepository: ActivityLogRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `when activity is logged, leaderboard should be updated via event`() = runBlocking {
        // Given
        val uniqueId = UUID.randomUUID().toString()
        val user = User(
            dbUsername = "user_$uniqueId",
            email = "test_$uniqueId@event.com",
            dbPassword = "password"
        )
        val savedUser = userRepository.save(user)
        val userId = savedUser.id

        val command = LogActivityCommand(
            userId = userId,
            type = ActivityType.STEPS,
            value = 500,
            occurredAt = Instant.now(),
            rawData = mapOf("device" to "fitbit")
        )

        // When
        activityLogService.logActivity(command)

        // Then
        // Wait up to 2 seconds for the async listener to update Redis
        await().atMost(2, TimeUnit.SECONDS).untilAsserted {
            // We check the score directly for this user, ignoring where they stand in the global rank
            // (because DataSeeder might have populated the board with high scores)
            val score = leaderboardService.getUserScore(userId.toString())
            
            assertEquals(500.0, score, 0.01)
        }
    }
}
