[![ci](https://github.com/web-budget/back-end/actions/workflows/ci.yml/badge.svg)](https://github.com/web-budget/back-end/actions/workflows/ci.yml)

# webBudget back-end

Welcome to the back-end application for the webBudget project!

## Toolchain

| Component | Version |
| --- | --- |
| Kotlin | 2.3 |
| Spring Boot | 4.0 |
| Java (JDK) | 25 |
| Gradle | via the checked-in wrapper (`gradlew` / `gradlew.bat`) |
| PostgreSQL | 17 (Docker) |
| Liquibase | schema migrations |
| Detekt | static analysis |
| JUnit 5 + Testcontainers + MockK + AssertJ | testing |

## Prerequisites

Before you start, install:

1. **JDK 25** — [Liberica JDK](https://bell-sw.com/pages/downloads/) is the preferred distribution (it's the one homologated by Spring). Other distributions (Temurin, Zulu, etc.) will also work.
2. **Docker** — [Docker Desktop](https://docs.docker.com/get-docker/) on Windows/Mac, or Docker Engine on Linux.
   Required for running the app locally **and** for the integration test suite (Testcontainers).
3. **IntelliJ IDEA** — Community or Ultimate. Strongly recommended for Kotlin work.
   Download it [here](https://www.jetbrains.com/?from=webBudget) if you don't have it yet.
4. **Git** — for cloning and contributing.

You do **not** need to install Gradle separately — the project ships the Gradle wrapper.

## Project setup

Clone the repository, then from the project root:

```bash
# 1. Start the supporting services (PostgreSQL + Maildev)
docker compose -p web-budget up -d

# 2. Build and run the test suite
./gradlew clean build
```

`clean build` cleans previous outputs, runs [Detekt](https://detekt.github.io/detekt/), runs the test suite, and compiles. A `BUILD SUCCESSFUL` message at the end means you're ready to go.

To run only the linter:

```bash
./gradlew detekt
```

The first run may auto-fix simple issues; a second run reports only the ones that need manual attention.

To start the application locally:

```bash
./gradlew bootRun
```

The API will be available on `http://localhost:8085`. Maildev's web UI runs on `http://localhost:1080`.

## IntelliJ IDEA setup

1. **Open the project** as a Gradle project — IntelliJ will detect `build.gradle.kts` and import automatically.
2. **Project SDK** — set to JDK 25 (`File → Project Structure → Project`).
3. **Gradle JVM** — also JDK 25 (`Settings → Build, Execution, Deployment → Build Tools → Gradle`). Set _Build and run using_ and _Run tests using_ to **Gradle** for fidelity with CI, or **IntelliJ IDEA** for faster local iteration.
4. **Enable annotation processing** — `Settings → Build, Execution, Deployment → Compiler → Annotation Processors` (needed by Spring Boot configuration metadata).
5. **Recommended plugins** — Kotlin (bundled), Spring Boot (bundled in Ultimate), Detekt, Docker, Database Tools (Ultimate).
6. **Code style** — IntelliJ's default Kotlin style with 4-space indentation matches the project conventions.

After import, run the `Application.kt` main class or use `./gradlew bootRun`.

## Local services

`docker compose -p web-budget up -d` starts:

- **PostgreSQL 17** on `localhost:5433` — database `webbudget`, user/password `sa_webbudget` / `sa_webbudget`.
- **Maildev** — SMTP on `localhost:1025` (user/password `maildev` / `maildev`), web UI on `http://localhost:1080`.

Stop them with `docker compose -p web-budget down`.

## Further reading

Project documentation for contributors lives under [`docs/`](docs/):

- [Project structure](docs/project-structure.md)
- [Architecture and patterns](docs/architecture.md)
- [Development commands and environment](docs/development.md)
- [Testing](docs/testing.md)
- [Security](docs/security.md)
- [Contribution workflow](docs/workflow.md)

## FAQ

- **Why separate front-end and back-end?** It lowers the barrier to contributing — newcomers don't have to navigate a single huge codebase with tricky cross-stack configuration. It also lets each side use the best tools for the job.
- **How can I start contributing?** Check the [project board](https://github.com/orgs/web-budget/projects/6) for open tickets.
- **Why Kotlin instead of Java?** Kotlin gives us a more complete functional toolset and concise syntax while staying fully interoperable with the Java ecosystem we already rely on (Spring, Hibernate, Testcontainers).
