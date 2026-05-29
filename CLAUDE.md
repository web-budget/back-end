# Project Guidelines

Kotlin / Spring Boot 4 backend for the webBudget personal-budget application.

## Toolchain

- Kotlin 2.3 on Java 25
- Spring Boot 4
- PostgreSQL 17 (via Docker)
- Liquibase for schema migrations — never change the schema manually or via `ddl-auto: create/update`.

## Architecture at a glance

Three layers under `src/main/kotlin/br/com/webbudget`:

- `application/` — REST controllers, payloads, mappers. No business logic.
- `domain/` — services, entities, validators, events. All business rules.
- `infrastructure/` — Spring config, JPA repositories, utilities.

See [`docs/architecture.md`](docs/architecture.md) for the entity, repository, and validator patterns.

## Coding style

- 4-space indentation. `PascalCase` types, `camelCase` members, `UPPER_SNAKE_CASE` constants.
- Kotlin-native — no Lombok.
- Use the type system; do not add null checks for non-null types.
- Keep service methods focused; prefer small private helpers over long methods.
- Return typed domain objects, not maps or loosely typed structures.

## Reference docs

- [`docs/project-structure.md`](docs/project-structure.md) — directory layout and where things go.
- [`docs/architecture.md`](docs/architecture.md) — three-layer architecture, entity/repository/validator patterns.
- [`docs/development.md`](docs/development.md) — prerequisites, Gradle commands, Docker services, env vars.
- [`docs/testing.md`](docs/testing.md) — test stack, `UTest` vs `ITest`, fixtures and SQL setup.
- [`docs/security.md`](docs/security.md) — JWT, roles, password hashing, CORS.
- [`docs/workflow.md`](docs/workflow.md) — branching, commits, PR rules.

## Quick checks before any PR

```bash
./gradlew detekt
./gradlew test
```

Both must pass. Docker must be running for integration tests.
