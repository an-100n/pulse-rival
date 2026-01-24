package com.pulserival.gamification.dto

data class LeaderboardEntry(
    val rank: Long,
    val userId: String,
    val score: Double
)
