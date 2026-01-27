package com.pulserival.activity.event

import com.pulserival.activity.entity.ActivityType
import java.util.UUID

/**
 * Event published when a user successfully logs an activity.
 * This decouples the core Activity logic from downstream effects like Gamification.
 */
data class ActivityLoggedEvent(
    val userId: UUID,
    val type: ActivityType,
    val value: Int
)
