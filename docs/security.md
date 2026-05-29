# Security

## Authentication

- **OAuth2 Resource Server** with JWT bearer tokens (`spring-boot-starter-oauth2-resource-server`).
- JWTs are signed with **HMAC-SHA256** using a shared secret. The secret is read from `web-budget.jwt.secret` (env var `APPLICATION_JWT_SECRET`) and **must be at least 32 characters / 256 bits**. Never commit a real secret.
- Tokens are accepted from either the `Authorization: Bearer …` header or the `wb-auth` cookie (see `cookieOrHeaderBearerTokenResolver` in `SecurityConfiguration.kt`).
- Sessions are stateless; CSRF is disabled because the API is token-based.

## Authorization

Roles live in `br.com.webbudget.domain.entities.administration.Role`:

| Role | Path prefix |
| --- | --- |
| `ADMINISTRATION` | `/api/administration/**` |
| `REGISTRATION` | `/api/registration/**` |
| `FINANCIAL` | `/api/financial/**` |
| `DASHBOARDS` | `/api/dashboards/**` |
| `INVESTMENTS` | `/api/investments/**` |

Public endpoints: `/actuator/health/**`, `/actuator/info/**`, `/accounts/**`, `/auth/**`. Everything else requires authentication.

When adding a new endpoint, scope it to the appropriate role in `SecurityConfiguration.configureSecurity` rather than relying on `@PreAuthorize` alone.

## Passwords

User passwords are encoded with **BCrypt at strength 11** (`BCryptPasswordEncoder(11)`). Never store, log, or echo plaintext passwords.

## CORS

CORS is configured for the frontend origin from `web-budget.frontend-url` (env var `APPLICATION_FRONTEND_URL`). Credentials are allowed so the `wb-auth` cookie can be sent cross-origin during development.
