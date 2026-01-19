package com.pulserival.activity.repository

import com.pulserival.activity.entity.ActivityLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ActivityLogRepository : JpaRepository<ActivityLog, UUID> {
    fun findAllByUserId(userId: UUID): List<ActivityLog>
}
