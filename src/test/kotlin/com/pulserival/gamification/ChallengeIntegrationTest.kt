package com.pulserival.gamification

import com.pulserival.gamification.entity.ChallengeStatus
import com.pulserival.gamification.entity.ChallengeType
import com.pulserival.gamification.service.ChallengeService
import com.pulserival.identity.entity.User
import com.pulserival.identity.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@Testcontainers
class ChallengeIntegrationTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:16-alpine")

        @Container
        val redis = GenericContainer("redis:7.2-alpine").withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }
        }
    }

    @Autowired
    private lateinit var challengeService: ChallengeService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should create challenge and allow user to join`() {
        // Given a user exists
        val user = userRepository.save(User(
            dbUsername = "vault_racer",
            email = "racer@example.com",
            dbPassword = "encoded_password"
        ))

        val startAt = Instant.now().plus(1, ChronoUnit.HOURS)
        val endAt = startAt.plus(7, ChronoUnit.DAYS)

        // When creating a challenge
        val challenge = challengeService.createChallenge(
            title = "The 50k Step Sprint",
            description = "First to 50k steps wins the elite share of the vault.",
            type = ChallengeType.STEP_COUNT,
            goalValue = 50000.0,
            startAt = startAt,
            endAt = endAt
        )

        // Then challenge should be created with zero vault total
        assertNotNull(challenge.id)
        assertEquals(0.0, challenge.vaultTotalPoints)

        // When user joins
        val participation = challengeService.joinChallenge(challenge.id, user.id)

        // Then participation should be recorded and vault should grow
        assertNotNull(participation.id)
        val updatedChallenge = challengeService.getChallenge(challenge.id)
        assertEquals(100.0, updatedChallenge.vaultTotalPoints, "Vault should increase by 100 on join")
    }
}
