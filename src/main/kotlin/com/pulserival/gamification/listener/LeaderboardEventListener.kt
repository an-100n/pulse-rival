package com.pulserival.gamification.listener

import com.pulserival.activity.event.ActivityLoggedEvent
import com.pulserival.gamification.service.LeaderboardService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class LeaderboardEventListener(
    private val leaderboardService: LeaderboardService
) {

    /**
     * Listens for ActivityLoggedEvent and updates the leaderboard.
     * 
     * @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT):
     * Ensures we ONLY update Redis if the data was successfully saved to Postgres.
     * Prevents "Ghost Points" (updating Redis then rolling back the DB).
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleActivityLogged(event: ActivityLoggedEvent) {
        // In the future, we might have complex XP logic here.
        // For now, 1 unit of value (e.g., 1 step) = 1 point.
        val scoreDelta = event.value.toDouble()
        
        leaderboardService.updateScore(event.userId, scoreDelta)
    }
}
