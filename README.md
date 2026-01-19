# PulseRival (Berlin 2026 Edition)
> **High-Performance Health Gamification Engine**
> *Status: Active Development*

## 🚀 Project Mission
PulseRival is a backend-first competitive platform that ingests high-frequency health data (steps, heart rate, calories) and powers real-time social leaderboards. 

Unlike standard CRUD apps, PulseRival solves:
1.  **High-Volume Ingestion:** Processing concurrent webhook streams from health providers.
2.  **Temporal Consistency:** Handling "Daily Streaks" across 30+ user timezones.
3.  **Real-Time Rankings:** Utilizing Redis Z-Sets for live leaderboards (O(log N)).
4.  **Privacy-First Design:** Implementing Row-Level Security (RLS) concepts via Spring Security.

## 🛠 Tech Stack (The "Senior Junior" Stack)
*   **Language:** Kotlin 2.1 (Coroutines, Flow, Value Classes)
*   **Framework:** Spring Boot 3.5+ (Virtual Threads enabled)
*   **Database:** PostgreSQL 16 (Partitioning, JSONB)
*   **Cache:** Redis 7 (Sorted Sets for Leaderboards)
*   **Security:** Spring Security 6 (Stateless JWT, OAuth2 Resource Server)
*   **Testing:** JUnit 5, Testcontainers, MockK
*   **Build:** Gradle (Kotlin DSL)

## 🏗 Architecture
We follow **Domain-Driven Design (DDD)** principles, separating business logic from infrastructure.

```text
com.pulserival.api
├── domain          # Pure Business Logic (No Spring dependencies)
│   ├── model       # Entities (User, Challenge, ActivityLog)
│   └── service     # Domain Services (ScoringEngine, StreakCalculator)
├── application     # Use Cases & Orchestration
│   ├── port        # Input/Output Ports
│   └── service     # Application Services
├── infrastructure  # Adapters (Spring, Postgres, Redis)
│   ├── web         # REST Controllers
│   ├── persistence # JPA Repositories
│   └── security    # JWT Filters
└── config          # Spring Configuration
```

## ⚡ Quick Start
1.  **Start Infrastructure:**
    ```bash
    docker compose up -d
    ```
2.  **Run App:**
    ```bash
    ./gradlew bootRun
    ```
3.  **Verify:**
    `GET http://localhost:8080/actuator/health`

## 📈 Key Engineering Challenges
*   **Idempotency:** Preventing double-counting of step updates.
*   **Timezone Math:** calculating "Daily Goals" based on `ZoneId`.
*   **Concurrency:** Optimistic locking on Challenge Scoreboards.

---
*Author: Ion Lazarev*
