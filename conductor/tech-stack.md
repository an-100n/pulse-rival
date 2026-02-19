# Tech Stack: PulseRival

## 🛠 Core Development
- **Language:** Kotlin 2.2.21 (JVM 21) - Utilizing Coroutines, Flow, and Value Classes.
- **Framework:** Spring Boot 4.0.1 - Focusing on Virtual Threads and stateless architecture.
- **Build System:** Gradle (Kotlin DSL).

## 🗄 Data & Performance
- **Primary Database:** PostgreSQL 16 - Using Partitioning and JSONB for flexible health data storage.
- **Caching & Real-time:** Redis 7 - Leveraging Sorted Sets (Z-Sets) for O(log N) leaderboard performance.

## 🔐 Security & Communication
- **Authentication:** Spring Security 6 - Implementing stateless JWT with custom filters.
- **API Documentation:** SpringDoc OpenAPI 2.8.3 (Swagger UI).

## 🧪 Testing & Quality
- **Framework:** JUnit 5.
- **Integration Testing:** Testcontainers (PostgreSQL & Redis modules) with automated Podman detection.
- **Mocking:** MockK.
- **Async Verification:** Awaitility.

## 🐳 Infrastructure
- **Containerization:** Docker Compose / Podman for local development and integration tests.
