package com.pulserival.activity.service

import com.pulserival.activity.dto.ActivityLogResponse
import com.pulserival.activity.dto.LogActivityCommand
import com.pulserival.activity.event.ActivityLoggedEvent
import com.pulserival.common.exception.InvalidActivityValueException
import com.pulserival.common.exception.UserNotFoundException
import com.pulserival.activity.entity.ActivityLog
import com.pulserival.activity.repository.ActivityLogRepository
import com.pulserival.identity.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ActivityLogService(
    private val activityLogRepository: ActivityLogRepository,
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher
) {

    @Transactional
    suspend fun logActivity(command: LogActivityCommand): ActivityLogResponse {
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

        // Switch to IO thread ONLY for the blocking DB call
        val saved = withContext(Dispatchers.IO) {
            activityLogRepository.save(log)
        }

        // Publish Event: "Hey, an activity happened!"
        // Downstream listeners (Gamification, Analytics, etc.) will handle the rest.
        eventPublisher.publishEvent(
            ActivityLoggedEvent(
                userId = saved.userId,
                type = saved.type,
                value = saved.value
            )
        )

        return ActivityLogResponse(
            id = saved.id,
            type = saved.type,
            value = saved.value,
            occurredAt = saved.occurredAt,
            rawData = saved.rawData
        )
    }

    suspend fun getUserActivities(userId: UUID): List<ActivityLogResponse> = withContext(Dispatchers.IO) {
        activityLogRepository.findAllByUserId(userId).map {
            ActivityLogResponse(it.id, it.type, it.value, it.occurredAt, it.rawData)
        }
    }
}
