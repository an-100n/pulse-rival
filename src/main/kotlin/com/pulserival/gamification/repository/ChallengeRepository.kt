package com.pulserival.gamification.repository

import com.pulserival.gamification.entity.Challenge
import com.pulserival.gamification.entity.ChallengeStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ChallengeRepository : JpaRepository<Challenge, UUID> {
    fun findAllByStatus(status: ChallengeStatus): List<Challenge>
}
