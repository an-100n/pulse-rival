package com.pulserival.activity.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "activity_logs")
class ActivityLog(
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ActivityType,

    @Column(nullable = false)
    val value: Int,

    @Column(name = "occurred_at", nullable = false)
    val occurredAt: Instant,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_data", columnDefinition = "jsonb")
    val rawData: Map<String, Any> = emptyMap()
) {
    @Id
    val id: UUID = UUID.randomUUID()

    // equals() based ONLY on ID
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActivityLog) return false
        return id == other.id
    }

    // hashCode() based ONLY on ID
    override fun hashCode(): Int {
        return id.hashCode()
    }

    // toString() that is safe (doesn't dump everything)
    override fun toString(): String {
        return "ActivityLog(id=$id, type=$type, value=$value)"
    }
}