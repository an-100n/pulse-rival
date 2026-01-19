package com.pulserival.activity.dto

import com.pulserival.activity.entity.ActivityType
import java.time.Instant
import java.util.UUID

data class ActivityLogResponse(
    val id: UUID,
    val type: ActivityType,
    val value: Int,
    val occurredAt: Instant
)
