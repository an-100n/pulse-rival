package com.pulserival.gamification.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "participations",
    uniqueConstraints = [UniqueConstraint(columnNames = ["challenge_id", "user_id"])]
)
class Participation(
    @Column(name = "challenge_id", nullable = false)
    val challengeId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "current_value", nullable = false)
    var currentValue: Double = 0.0,

    @Column(name = "is_completed", nullable = false)
    var isCompleted: Boolean = false,

    @Column(name = "completed_at", nullable = true)
    var completedAt: Instant? = null,

    @Column(name = "entry_weight", nullable = false)
    var entryWeight: Double = 0.0,

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    val joinedAt: Instant = Instant.now()
) {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Participation) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Participation(id=$id, userId=$userId, challengeId=$challengeId, isCompleted=$isCompleted)"
    }
}
