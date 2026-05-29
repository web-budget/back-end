# Development

## Prerequisites

- **JDK 25** (Temurin or any distribution that ships Java 25).
- **Docker** (Docker Desktop on Windows/Mac, or Docker Engine on Linux). Required for both local runtime and integration tests.
- **IntelliJ IDEA** (Community or Ultimate). Strongly recommended over other IDEs for Kotlin work — see the README for setup notes.

The Gradle wrapper (`gradlew` / `gradlew.bat`) is checked in, so no separate Gradle install is needed.

## Local services

Start PostgreSQL 17 and Maildev with:

```bash
docker compose -p web-budget up -d
```

Service ports:

- PostgreSQL — `localhost:5433` (db `webbudget`, user/password `sa_webbudget`).
- Maildev SMTP — `localhost:1025` (user/password `maildev`).
- Maildev UI — `http://localhost:1080`.

## Gradle commands

Run all commands from the repository root.

| Command | What it does |
| --- | --- |
| `./gradlew clean build` | Cleans, lints (Detekt), runs the full test suite, and compiles. |
| `./gradlew test` | Runs unit and integration tests. Requires Docker for integration tests. |
| `./gradlew bootRun` | Starts the service locally on port `8085`. Requires Docker (Postgres + Maildev). |
| `./gradlew detekt` | Runs Kotlin static analysis. Auto-fixes simple issues on first run. |
| `./gradlew clean` | Removes `build/` outputs. |

## Spring profiles

- `local` — developer machine defaults.
- `dev` — shared dev environment.
- `prod` — production.
- `test` — used automatically by integration tests.

Profile-specific overrides live in `src/main/resources/config/application-{profile}.yml`.

## Required environment variables (production)

- `APPLICATION_JWT_SECRET` — HMAC-SHA256 secret, at least 32 characters (256 bits).
- `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` — Postgres connection.
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USER`, `MAIL_PASSWORD` — SMTP credentials.
- `APPLICATION_FRONTEND_URL` — used for CORS and email links.

Defaults in `application.yml` cover the local development setup.
