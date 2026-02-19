# Product Guidelines: PulseRival Backend

## 🗣 Brand Voice (API & DX)
The PulseRival backend should feel **Playful & Friendly** to the developers building on top of it. Even though it is a high-performance engine, it should avoid sounding "robotic" or "cold."

- **Encouraging Errors:** Use informative, context-aware error messages that maintain the project's spirit. 
    - *Bad:* `403: Challenge expired.`
    - *Good:* `The Vault is locked! This challenge window has closed. Join the next Global Vault to start a new race.`

## 🧠 Domain Logic Principles
The logic within the engine must prioritize the "Social & Fair" aspect of competition.

- **Tie-Breaking:** In the event of a millisecond-level tie for a Velocity Tier, the system should **Split the Prize**. Both participants receive the higher weight. This reinforces the "Friendly" brand voice and encourages co-operative competition within groups.
- **Velocity Transparency:** The API should always provide clear "Why" metadata for scoring. If a user is in the "Pro Tier," the response should include the time-remaining or speed-threshold that placed them there.

## 🛠 Engineering Tone
- **Responsive by Default:** "Velocity" is in the name. The backend should prioritize low latency for events that trigger social notifications (Nudges, Taunts).
- **Domain-First Naming:** Code and API endpoints should use the terminology of "The Velocity Vault" (e.g., `/vaults/{id}/burst`, `VaultTier.ELITE`).
