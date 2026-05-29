# Project Structure

Production code lives in `src/main/kotlin/br/com/webbudget`, organized into three layers:

```
br.com.webbudget/
├── Application.kt          # Spring Boot entry point
├── application/            # REST controllers, payloads (DTOs), mappers
├── domain/                 # Business logic, JPA entities, services, validators, events
└── infrastructure/         # Spring config, JPA repositories, utilities
```

## Resources

- `src/main/resources/config/` — `application.yml` and profile-specific overrides (`application-{dev,local,prod}.yml`).
- `src/main/resources/db/changelog/` — Liquibase changelogs. Master file: `db.changelog.master.xml`.
- `src/main/resources/mail-templates/` — Thymeleaf templates for outgoing email.
- `src/main/resources/banner.txt` — Spring Boot startup banner.

## Tests

Tests mirror the production package structure under `src/test/kotlin/br/com/webbudget`.

- `src/test/resources/config/application-test.yml` — overrides used by the `test` Spring profile.
- `src/test/resources/payloads/` — JSON fixtures for controller integration tests.
- `src/test/resources/sql/` — SQL setup scripts loaded with `@Sql` for integration tests.
- `src/test/resources/db/changelog/db.changelog.testing.xml` — test-only Liquibase changelog.
- `src/test/resources/logback-test.xml` — test logging configuration.

## Other

- `docker-compose.yml` — PostgreSQL 17 and Maildev for local development.
- `config/` — project tooling configuration (Detekt rules, etc.).
- `build.gradle.kts` / `settings.gradle.kts` — Gradle Kotlin DSL build scripts.
