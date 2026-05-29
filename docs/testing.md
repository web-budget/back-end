# Testing

## Stack

- **JUnit 5** with the Spring Boot Test starters.
- **Testcontainers** — PostgreSQL and Mailpit (via `ch.martinelli.oss:testcontainers-mailpit`).
- **MockK** (via `com.ninja-squad:springmockk`) for mocking.
- **AssertJ** for fluent assertions.
- **JSON Unit** (`json-unit-assertj`, `json-unit-spring`) for JSON payload assertions.
- **Awaitility** for asynchronous assertions.

## Test classification

Tests are split by suffix:

- **`*UTest`** — pure unit tests. No Spring context, no database. Example: `UserMapperUTest.kt`.
- **`*ITest`** — integration tests. Boot the Spring context and require Docker (Testcontainers). Example: `UserServiceITest.kt`.

Integration tests extend one of:

- `BaseIntegrationTest` — services, repositories, domain-level integration.
- `BaseControllerIntegrationTest` — REST controllers (adds MockMvc and security plumbing).

## Test resources

- `src/test/resources/payloads/` — JSON fixtures for controller tests. Load them with the standard test helpers rather than embedding JSON in Kotlin strings.
- `src/test/resources/sql/` — SQL setup/teardown scripts. Reference them with `@Sql` on the test class or method.
- `src/test/resources/config/application-test.yml` — profile overrides used by the `test` profile.
- `src/test/resources/db/changelog/db.changelog.testing.xml` — additional Liquibase changes applied only in tests.

## Running tests

```bash
./gradlew test
```

Tests run in parallel forks (`maxParallelForks = availableProcessors()`). Docker must be running for any `*ITest`.

Run `./gradlew test` (and `./gradlew detekt`) before opening any PR — both must pass.
