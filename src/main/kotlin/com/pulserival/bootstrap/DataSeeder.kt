package com.pulserival.bootstrap

import com.pulserival.activity.entity.ActivityLog
import com.pulserival.activity.entity.ActivityType
import com.pulserival.activity.repository.ActivityLogRepository
import com.pulserival.gamification.service.LeaderboardService
import com.pulserival.identity.entity.Sex
import com.pulserival.identity.entity.User
import com.pulserival.identity.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.random.Random

@Configuration
@Profile("!test") // Don't run this during unit tests
class DataSeeder(
    private val userRepository: UserRepository,
    private val activityLogRepository: ActivityLogRepository,
    private val passwordEncoder: PasswordEncoder,
    private val leaderboardService: LeaderboardService
) {

    @Bean
    fun seedData(): CommandLineRunner {
        return CommandLineRunner {
            if (userRepository.count() > 0) {
                println("🌱 Database already seeded. Skipping.")
                return@CommandLineRunner
            }

            println("🌱 Seeding Users and Activities...")

            val commonPassword = passwordEncoder.encode("password") ?: "password"
            val users = mutableListOf<User>()

            // 1. Create 10 Users
            for (i in 1..10) {
                val sex = if (Random.nextBoolean()) Sex.MALE else Sex.FEMALE
                // Height: Integer (e.g. 175.0)
                val heightCm = Random.nextInt(160, 190).toDouble()
                // Weight: 1 decimal place (e.g. 72.5)
                val rawWeight = Random.nextDouble(60.0, 100.0)
                val weightKg = Math.round(rawWeight * 10.0) / 10.0
                val birthDate = LocalDate.now().minusYears(Random.nextLong(18, 50))

                val user = User(
                    dbUsername = "user_$i",
                    email = "user_$i@example.com",
                    dbPassword = commonPassword,
                    timezone = "UTC",
                    sex = sex,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    birthDate = birthDate
                )
                users.add(user)
            }
            userRepository.saveAll(users)
            println("✅ Created 10 Users")

            // 2. Create Activities for each user
            val activities = mutableListOf<ActivityLog>()
            val now = Instant.now()

            users.forEach { user ->
                // Generate 5-10 random activities for each user
                val activityCount = Random.nextInt(5, 11)
                
                repeat(activityCount) {
                    val daysAgo = Random.nextLong(0, 30)
                    val occurredAt = now.minus(daysAgo, ChronoUnit.DAYS)
                    
                    val type = if (Random.nextBoolean()) ActivityType.STEPS else ActivityType.CALORIES
                    val value = Random.nextInt(100, 5000)
                    
                    val rawData = mapOf(
                        "source" to "Seeder",
                        "originalId" to UUID.randomUUID().toString(),
                        "meta" to "random-data-${Random.nextInt(100)}"
                    )

                    activities.add(
                        ActivityLog(
                            userId = user.id,
                            type = type,
                            value = value,
                            occurredAt = occurredAt,
                            rawData = rawData
                        )
                    )
                    
                    // Backfill Redis Leaderboard
                    leaderboardService.updateScore(user.id, value.toDouble())
                }
            }
            activityLogRepository.saveAll(activities)
            println("✅ Created ${activities.size} Activity Logs")
            println("🔥 Backfilled Redis Leaderboard")
            println("🌱 Seeding Complete!")
        }
    }
}
