package com.pulserival.gamification.service

import com.pulserival.gamification.dto.LeaderboardEntry
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LeaderboardService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val leaderboardKey = "leaderboard:global"

    /**
     * Updates the user's score in the global leaderboard.
     * Uses ZINCRBY: O(log(N)) complexity.
     */
    fun updateScore(userId: UUID, scoreDelta: Double) {
        redisTemplate.opsForZSet().incrementScore(leaderboardKey, userId.toString(), scoreDelta)
    }

    /**
     * Retrieves the top N users from the leaderboard.
     * Uses ZREVRANGE: O(log(N) + M) complexity.
     */
    fun getGlobalLeaderboard(limit: Int = 10): List<LeaderboardEntry> {
        // Redis uses 0-based inclusive indices.
        // To get top 10 (rank 1-10), we request range 0 to 9.
        val endIndex = (limit - 1).toLong()
        
        val tupleSet = redisTemplate.opsForZSet()
            .reverseRangeWithScores(leaderboardKey, 0, endIndex)
            ?: emptySet()

        // Map Redis Tuple to our DTO
        // Note: ZREVRANGE returns results ordered by score, so the first item is Rank 1.
        return tupleSet.mapIndexed { index, tuple ->
            LeaderboardEntry(
                rank = (index + 1).toLong(),
                userId = tuple.value ?: "unknown",
                score = tuple.score ?: 0.0
            )
        }
    }
}
