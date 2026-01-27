# Session Log

## Project Status
*   **Phase:** Phase 6 - Event-Driven Architecture (In Progress)
*   **Current Goal:** Decouple Services using Spring Events.
*   **Last Update:** January 27, 2026

## Recent Achievements
*   **Decoupling:** Refactored `ActivityLogService` to remove direct dependency on `LeaderboardService`.
    *   It now publishes an `ActivityLoggedEvent` via `ApplicationEventPublisher`.
    *   This prevents "God Objects" and increases system resilience (if Redis fails, Activity Logging still succeeds... theoretically, though the listener is currently synchronous).
*   **Event Listener:** Implemented `LeaderboardEventListener` to consume the event and update Redis.
*   **Verification:** Created `EventWiringTest` to verify the `Service -> Event -> Listener -> Redis` flow using Testcontainers.
    *   Used `@MockitoBean` (modern Spring Boot test support) to mock SQL repositories while keeping the Redis container real.
    *   Handled pre-existing data from `DataSeeder` in assertions.

## Key Decisions & Lessons
*   **Event-Driven Thinking:** Used the "PA System" analogy. The Activity Service "shouts" that an activity happened; it doesn't care who listens.
*   **Synchronous vs Asynchronous:** Currently, the listener is synchronous (default in Spring). We discussed that `@Async` would make it truly non-blocking.
*   **Testing Side Effects:** Verified that we can test the "wiring" of events without spinning up the entire database (using Mocking).

## Next Steps
1.  **Async Execution:** Enable `@Async` and `@EnableAsync` to make the Listener truly background processing.
2.  **Reliability:** Discuss what happens if the Listener fails? (Dead Letter Queues? Retries?).
3.  **Phase 7:** Maybe look into advanced Redis features or a new module (like Analytics).