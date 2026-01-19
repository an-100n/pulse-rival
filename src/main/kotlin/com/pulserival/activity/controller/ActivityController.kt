package com.pulserival.activity.controller

import com.pulserival.activity.service.ActivityLogResponse
import com.pulserival.activity.service.ActivityLogService
import com.pulserival.activity.service.LogActivityCommand
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/activities")
class ActivityController(
    private val activityLogService: ActivityLogService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun logActivity(@RequestBody command: LogActivityCommand): ActivityLogResponse {
        return activityLogService.logActivity(command)
    }

    @GetMapping("/user/{userId}")
    fun getActivities(@PathVariable userId: UUID): List<ActivityLogResponse> {
        return activityLogService.getUserActivities(userId)
    }
}
