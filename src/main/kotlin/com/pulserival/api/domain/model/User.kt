package com.pulserival.api.domain.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@JvmInline
value class UserId(val value: UUID) {
    companion object {
        fun random() = UserId(UUID.randomUUID())
        fun fromString(s: String) = UserId(UUID.fromString(s))
    }
}

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val timezone: String = "UTC", // e.g., "Europe/Berlin"

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()
) {
    // Domain Logic inside the Entity (Rich Model)
    fun changeTimezone(newZone: String): User {
        // In a real app, we would validate the ZoneId string here
        return this.copy(timezone = newZone)
    }
}
