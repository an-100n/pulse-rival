package com.pulserival.gamification.controller

import com.pulserival.gamification.dto.LeaderboardEntry
import com.pulserival.gamification.service.LeaderboardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/leaderboards")
class LeaderboardController(
    private val leaderboardService: LeaderboardService
) {

    @GetMapping("/global")
    fun getGlobalLeaderboard(): List<LeaderboardEntry> {
        return leaderboardService.getGlobalLeaderboard()
    }
}
