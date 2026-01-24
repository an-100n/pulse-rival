# Session Log

## Project Status
*   **Phase:** Phase 4 - High Performance (Redis & Gamification)
*   **Current Goal:** Implement real-time Global Leaderboards using Redis Sorted Sets.
*   **Last Update:** January 24, 2026

## Recent Achievements
*   **Infrastructure:** Configured `RedisTemplate` with String serialization in `RedisConfig.kt`.
*   **Service Layer:** Implemented `LeaderboardService` to handle `ZINCRBY` (update score) and `ZREVRANGE` (get top users).
*   **Integration:**
    *   Updated `ActivityLogService` to synchronously update the leaderboard when an activity is saved.
    *   Updated `DataSeeder` to backfill Redis with scores for seeded data.
*   **API:** Created `LeaderboardController` exposing `GET /api/v1/leaderboards/global`.
*   **Domain Logic:** Decision made to use `String` for User IDs in the Redis layer (pass-through) rather than parsing UUIDs, for performance and robustness.

## Key Decisions & Lessons
*   **Redis Serialization:** Used `StringRedisSerializer` to ensure keys are human-readable in `redis-cli`, facilitating easier debugging.
*   **UUID vs String in DTO:** Opted for `String` in `LeaderboardEntry` to avoid unnecessary parsing/serialization overhead in the high-performance read path.
*   **Sync vs Async:** Currently using synchronous updates to Redis for simplicity. Noted that in a production microservice, this should be eventually consistent via Domain Events to decouple availability.

## Next Steps
1.  **Verification:** Run the application and verify the endpoint manually via `posting`.
2.  **Reliability (Phase 5):** Write an Integration Test using Testcontainers (Redis) to ensure the leaderboard logic works in a clean, reproducible environment.
3.  **Refactoring:** Consider moving the Redis update logic to a Spring Event Listener (`@EventListener`) to decouple `ActivityLogService` from `LeaderboardService`.
