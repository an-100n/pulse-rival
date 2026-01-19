package com.pulserival.activity.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

enum class ActivityType {
    STEPS,
    CALORIES,
    DISTANCE_METERS,
    HEART_RATE_AVG
}

@Entity
@Table(name = "activity_logs")
data class ActivityLog(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID, // We store the ID, not the Object, to keep domains decoupled (DDD preference)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ActivityType,

    @Column(nullable = false)
    val value: Int,

    @Column(name = "occurred_at", nullable = false)
    val occurredAt: Instant
)
