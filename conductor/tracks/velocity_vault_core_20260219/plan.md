# Implementation Plan: Velocity Vault Core Engine

## Phase 1: Foundation - Challenge & Participation Models
- [ ] Task: Define the Domain Models for `Challenge` and `Participation`.
    - [ ] Create `Challenge` entity with status, goal, and vault totals.
    - [ ] Create `Participation` entity to link Users to Challenges with status and timestamps.
    - [ ] Set up JPA Repositories for both.
- [ ] Task: Implement `ChallengeService` for basic lifecycle management.
    - [ ] Write tests for challenge creation and user registration.
    - [ ] Implement `createChallenge` and `joinChallenge`.
- [ ] Task: Conductor - User Manual Verification 'Foundation' (Protocol in workflow.md)

## Phase 2: The Weighting Engine
- [ ] Task: Implement the `WeightingService` logic.
    - [ ] Write unit tests for Tier calculation (Elite, Pro, Finisher) based on completion duration.
    - [ ] Write unit tests for the Tie-Breaking "Split the Prize" logic.
    - [ ] Implement the `calculateEntryWeight` function.
- [ ] Task: Integrate Activity Events with Participation.
    - [ ] Update `LeaderboardEventListener` (or create a new one) to update `Participation` progress when activities are logged.
    - [ ] Trigger `ChallengeCompletedEvent` when a user hits the goal.
- [ ] Task: Conductor - User Manual Verification 'Weighting Engine' (Protocol in workflow.md)

## Phase 3: The Burst - Reward Distribution
- [ ] Task: Implement the `BurstService` for reward distribution.
    - [ ] Write tests for the "Burst" logic: probability-based selection and Dividend calculation.
    - [ ] Implement the `burstVault` function.
- [ ] Task: Implement a Scheduled Task or Trigger to close challenges and trigger the Burst.
- [ ] Task: Conductor - User Manual Verification 'The Burst' (Protocol in workflow.md)

## Phase 4: Integration & API Exposure
- [ ] Task: Expose Challenge and Vault status via REST.
    - [ ] Add endpoints to `LeaderboardController` or a new `ChallengeController`.
- [ ] Task: Final End-to-End Integration Test.
    - [ ] Script a full scenario: Start Challenge -> Log Activities -> Verify Weight -> Trigger Burst -> Verify Rewards.
- [ ] Task: Conductor - User Manual Verification 'Integration' (Protocol in workflow.md)
