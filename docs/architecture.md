# Architecture

Three-layer architecture. Dependencies flow inward: `application` → `domain` ← `infrastructure`.

1. **Application** — presentation layer. REST controllers, request/response payloads, and DTO mappers. No business logic here.
2. **Domain** — business layer. Services, entities, validators, domain events, and custom exceptions. All business rules live here.
3. **Infrastructure** — technical layer. Spring configuration classes, JPA repositories, and utility functions. Accessed by both application and domain layers.

## Key Patterns

### Entities

All entities extend `PersistentEntity<Long>` and expose an external UUID for the API. **Never expose the internal numeric ID externally** — payloads and URL identifiers always use the UUID.

### Database schemas

Schema names are centralized in `DatabaseSchemas.kt`. Reference schema constants from there in `@Table(schema = ...)` annotations rather than hard-coding strings.

### Repositories

Repositories extend `BaseRepository<T>`. For complex queries, define JPA `Specification`s as static helpers inside a companion object named `Specifications` (e.g., `UserRepository.Specifications`).

### Validators

Validation is centralized via three injection annotations:

- `@OnCreateValidation` — validators that run before persisting a new entity.
- `@OnUpdateValidation` — validators that run before updating an existing entity.
- `@OnDeleteValidation` — validators that run before deleting an entity.

Services collect the injected validator chain and execute it before any persistence operation. Add new validators by implementing the appropriate interface and annotating the bean.

### Schema migrations

Database schema is owned by Liquibase. Never modify the schema manually, and never set `spring.jpa.hibernate.ddl-auto` to anything other than `validate`. Add a new changeset under `src/main/resources/db/changelog/` and reference it from `db.changelog.master.xml`.
