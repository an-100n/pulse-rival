package com.pulserival.gamification.service

import com.pulserival.gamification.entity.Challenge
import com.pulserival.gamification.entity.ChallengeStatus
import com.pulserival.gamification.entity.ChallengeType
import com.pulserival.gamification.entity.Participation
import com.pulserival.gamification.repository.ChallengeRepository
import com.pulserival.gamification.repository.ParticipationRepository
import com.pulserival.identity.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class ChallengeService(
    private val challengeRepository: ChallengeRepository,
    private val participationRepository: ParticipationRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createChallenge(
        title: String,
        description: String,
        type: ChallengeType,
        goalValue: Double,
        startAt: Instant,
        endAt: Instant,
        isPrivate: Boolean = false
    ): Challenge {
        val challenge = Challenge(
            title = title,
            description = description,
            type = type,
            goalValue = goalValue,
            startAt = startAt,
            endAt = endAt,
            isPrivate = isPrivate
        )
        return challengeRepository.save(challenge)
    }

    @Transactional
    fun joinChallenge(challengeId: UUID, userId: UUID): Participation {
        val challenge = challengeRepository.findById(challengeId)
            .orElseThrow { IllegalArgumentException("Challenge not found: $challengeId") }

        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User not found: $userId")
        }

        if (participationRepository.findByChallengeIdAndUserId(challengeId, userId).isPresent) {
            throw IllegalStateException("User $userId is already participating in challenge $challengeId")
        }

        val participation = Participation(
            challengeId = challengeId,
            userId = userId
        )

        // Increment vault total when a new user joins
        challenge.vaultTotalPoints += 100.0 // Standard contribution
        challengeRepository.save(challenge)

        return participationRepository.save(participation)
    }

    fun getChallenge(id: UUID): Challenge {
        return challengeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Challenge not found: $id") }
    }
}
