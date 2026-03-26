# Project Guidelines

## Project Structure

This is a Kotlin Spring Boot 4 backend for a personal budget application. Production code lives in `src/main/kotlin/br/com/webbudget`, organized into three layers:

```
br.com.webbudget/
├── application/        # REST controllers, DTOs (payloads), mappers
├── domain/             # Business logic, JPA entities, services, validators, events
└── infrastructure/     # Spring config, JPA repositories, utilities
```

Configuration files live in `src/main/resources/config/`. Liquibase migrations are in `src/main/resources/db/changelog/`. Tests mirror the production package structure under `src/test/kotlin/br/com/webbudget`.

## Architecture

Three-layer architecture:

1. **Application** — presentation layer. REST controllers, request/response payloads, and DTO mappers. No business logic here.
2. **Domain** — business layer. Services, entities, validators, domain events, and custom exceptions. All business rules live here.
3. **Infrastructure** — technical layer. Spring configuration classes, JPA repositories, and utility functions. Accessed by both application and domain layers.

**Database schemas:** `administration`, `registration`, `financial` — defined in `DatabaseSchemas.kt`.

**Validation pattern:** validators are injected via `@OnCreateValidation`, `@OnUpdateValidation`, `@OnDeleteValidation`. Services execute the validator chain before any persistence operation.

**Entity pattern:** all entities extend `PersistentEntity<Long>` with an external UUID for API exposure. Never expose the internal numeric ID externally.

**Repository pattern:** repositories extend `BaseRepository<T>` and use Specifications with companion object helpers (e.g., `UserRepository.Specifications`) for complex queries.

## Build, Test, and Development Commands

Use the Gradle wrapper from the repository root:

- `./gradlew clean build` — compiles and runs the full test suite.
- `./gradlew test` — runs unit and integration tests.
- `./gradlew bootRun` — starts the service locally (requires Docker for PostgreSQL).
- `./gradlew detekt` — runs Kotlin linting.
- `./gradlew clean` — removes build outputs.

Local dev requires Docker running (PostgreSQL 17 on port 5433, Maildev on port 1025). Start with `docker-compose up -d`.

## Coding Style & Naming Conventions

Follow Kotlin conventions with 4-space indentation:

- `PascalCase` for classes, interfaces, and data models.
- `camelCase` for functions, parameters, and variables.
- `UPPER_SNAKE_CASE` for constants.

Keep service methods focused and explicit. Prefer small private helpers over long methods. Return typed domain objects rather than maps or loosely typed structures. Do not use Lombok (project is Kotlin-native). Do not add unnecessary null checks — use Kotlin's type system instead.

## Testing Guidelines

Tests use JUnit 5, Spring Boot Test, Testcontainers (PostgreSQL), MockK, AssertJ, GreenMail (email), and JSON Unit.

- **Unit tests:** suffix `UTest` (e.g., `UserMapperUTest.kt`). No Spring context, no database.
- **Integration tests:** suffix `ITest` (e.g., `UserServiceITest.kt`). Extend `BaseIntegrationTest` or `BaseControllerIntegrationTest`. Require Docker.
- Test payloads (JSON fixtures) go in `src/test/resources/payloads/`.
- Test SQL setup scripts go in `src/test/resources/sql/`.

Run `./gradlew test` before opening any PR. Docker must be running for integration tests.

## Commit & Pull Request Guidelines

Use Conventional Commit style matching the repository history:

- `fix #38: correct invoice balance calculation`
- `ref #35: extract validation logic from service`

Pay attention to the usage: `ref` must be used for ongoing work, `fix` only when you finish it since when you commit the code GitHub will automatically close the issue linked to the commit message

PRs must:

1. Reference the linked issue or ticket.
2. Fill in all sections of the PR template (`.github/pull_request_template.md`): objective, what changed, how to test, additional context.
3. Include test evidence (command run and result).
4. Stay small — prefer under 20 changed files.
5. Target `main` and use **Squash and merge** only.

## Development Process

1. Create a new branch from `main` with a semantic prefix: `feat/...`, `fix/...`.
2. Implement changes following the three-layer architecture.
3. Write or update tests (unit + integration as appropriate).
4. Run `./gradlew detekt` and `./gradlew test` — both must pass.
5. Open a PR targeting `main` with the PR template filled in.
6. Merge only after approval, using **Squash and merge**.

## Security Notes

- JWT signing uses an RSA key pair in `src/main/resources/keys/`. Never commit real private keys.
- Roles: `ADMINISTRATION`, `REGISTRATION`, `FINANCIAL`, `DASHBOARDS`, `INVESTMENTS`. Always scope endpoints to the appropriate role.
- Passwords are encoded with BCrypt (strength 11). Never store or log plaintext passwords.

## Toolchain

- Kotlin 2.3 / Spring Boot 4 / Java 25
- PostgreSQL 17 (via Docker)
- Liquibase for schema migrations — never modify the database schema manually or via `ddl-auto: create/update`.
