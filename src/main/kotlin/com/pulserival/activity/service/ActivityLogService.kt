package com.pulserival.activity.service

import com.pulserival.activity.dto.ActivityLogResponse
import com.pulserival.activity.dto.LogActivityCommand
import com.pulserival.common.exception.InvalidActivityValueException
import com.pulserival.common.exception.UserNotFoundException
import com.pulserival.activity.entity.ActivityLog
import com.pulserival.activity.repository.ActivityLogRepository
import com.pulserival.gamification.service.LeaderboardService
import com.pulserival.identity.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ActivityLogService(
    private val activityLogRepository: ActivityLogRepository,
    private val userRepository: UserRepository,
    private val leaderboardService: LeaderboardService
) {

    @Transactional
    fun logActivity(command: LogActivityCommand): ActivityLogResponse {
        // Business Rule: We don't accept negative values for health activities
        if (command.value < 0) {
            throw InvalidActivityValueException("Activity value cannot be negative: ${command.value}")
        }

        if (!userRepository.existsById(command.userId)) {
            throw UserNotFoundException(command.userId.toString())
        }

        val log = ActivityLog(
            userId = command.userId,
            type = command.type,
            value = command.value,
            occurredAt = command.occurredAt,
            rawData = command.rawData
        )

        val saved = activityLogRepository.save(log)

        // Sync with Leaderboard (Fire-and-forget logic for now)
        // In a distributed system, this should be handled via Domain Events (e.g. ActivityLoggedEvent)
        // to ensure eventual consistency if Redis is down.
        leaderboardService.updateScore(command.userId, command.value.toDouble())

        return ActivityLogResponse(
            id = saved.id,
            type = saved.type,
            value = saved.value,
            occurredAt = saved.occurredAt,
            rawData = saved.rawData
        )
    }

    fun getUserActivities(userId: UUID): List<ActivityLogResponse> {
        return activityLogRepository.findAllByUserId(userId).map {
            ActivityLogResponse(it.id, it.type, it.value, it.occurredAt, it.rawData)
        }
    }
}
