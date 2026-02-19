# Track Specification: Velocity Vault Core Engine

## 🚀 Track Goal
Implement the core engine for the "Velocity Vault" gamification system. This includes managing challenges, calculating time-based entry weights (Elite, Pro, Finisher tiers), and the final "Burst" reward distribution logic.

## 🛠 Functional Requirements
- **Challenge Lifecycle:** Support creating, activating, and closing challenges.
- **Participation Registry:** Track which users join which challenges and their progress.
- **Speed Weighting Engine:**
    - Detect the "First Finisher" and start the 24-hour "Speed Bonus" clock for private challenges.
    - Calculate Entry Weights based on completion time:
        - **Elite Tier (<48hrs):** 10x Weight.
        - **Pro Tier (48–96hrs):** 5x Weight.
        - **Finisher Tier (<7 days):** 1x Weight.
- **The Burst (Reward Distribution):**
    - Logic to randomly select winners based on their entry weights.
    - Logic to calculate and distribute the "Finisher's Dividend" (a small, steady return for all finishers).
- **Tie-Breaking:** Implement the "Split the Prize" logic where tied users both receive the higher tier weight.

## 🏗 Technical Architecture
- **Module:** `gamification`
- **Data Models:**
    - `Challenge`: Type, goal, window, participants, vault_total.
    - `Participation`: UserID, ChallengeID, status, completion_time, entry_weight.
- **Services:**
    - `ChallengeService`: CRUD operations and lifecycle management.
    - `WeightingService`: Logic for calculating tiers and entries.
    - `BurstService`: The distribution engine for rewards.
- **Events:**
    - `ChallengeCompletedEvent`: Triggered when a user finishes a challenge.
    - `VaultBurstEvent`: Triggered when rewards are distributed.

## 🧪 Verification Plan
- **Unit Tests:**
    - Test weight calculation for each tier.
    - Test tie-breaking logic.
- **Integration Tests:**
    - Create a challenge, log activities for a user until completion, and verify the correct weight is assigned.
    - Simulate multiple users finishing at different times and verify the "Burst" distribution.
- **Manual Verification:** Use the `verify_leaderboard.sh` script or Bruno collection to check current vault status and rewards.
