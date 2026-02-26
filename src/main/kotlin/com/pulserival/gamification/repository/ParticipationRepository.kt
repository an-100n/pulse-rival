package com.pulserival.gamification.repository

import com.pulserival.gamification.entity.Participation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ParticipationRepository : JpaRepository<Participation, UUID> {
    fun findByChallengeIdAndUserId(challengeId: UUID, userId: UUID): Optional<Participation>
    fun findAllByChallengeId(challengeId: UUID): List<Participation>
    fun findAllByUserId(userId: UUID): List<Participation>
}
