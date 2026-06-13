# AGENTS.md — VN

## Quick start

```bash
docker compose up -d          # start PostgreSQL
./gradlew compileJava         # compile
./gradlew test                # tests (use H2, no PG needed)
```

## Architecture

Spring Boot 4.0.6 / Java 21 / Gradle / PostgreSQL.

**Package layout** — feature-based under `auth.mix.vn`:
- `v1/auth/` — JWT auth, login/register/refresh, SecurityConfig
- `v1/user/` — User entity + management endpoints
- `v1/role/` — Role entity + management endpoints
- `v1/permission/` — Permission entity + constants + management endpoints
- `authorization/` — dynamic endpoint permission guard (DB-driven)
- `common/` — BaseEntity, auditing, OpenApiConfig, error handling
- `seed/` — DataInitializer (runs once, skips if data exists)

Each versioned module follows: `entity/`, `controller/`, `service/`, `dto/`, `repository/`, `config/`.

## Security model (critical)

**Spring Security** (`SecurityConfig`): `.anyRequest().permitAll()` — all requests pass at the web level.

**Authorization** is enforced entirely by `EndpointPermissionFilter` (a custom `OncePerRequestFilter`):

| Rule | Behavior |
|---|---|
| No matching rule | 403 "No permission rule defined" |
| `enabled = false` | allow |
| `requiredPermission = null` | allow (public endpoint) |
| `requiredPermission` set | check auth + user has that granted authority |

Endpoint-permission rules live in the `endpoint_permissions` DB table, seeded by `DataInitializer`. Add new public endpoints by adding a rule with `null` permission — **never** add `.permitAll()` to `SecurityConfig`.

Swagger/OpenAPI paths (`/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`, `/api-docs/**`) are made public via endpoint rules, not SecurityConfig.

## Seed data

`DataInitializer` creates: permissions (all `PermissionName` constants), roles (ADMIN/MODERATOR/USER), admin user (`admin`/`admin123`), endpoint permission rules. Runs only when `permissionRepository.count() == 0`. To reseed: `docker compose down -v && docker compose up -d`.

## Tests

H2 in-memory (`src/test/resources/application.properties`). No PostgreSQL needed. Run with `./gradlew test`.

## Conventions (from CONVENTIONS.md)

- No magic strings → use `PermissionName`, `RoleName`, constants
- Thin controllers → delegate to services
- DTO suffix → `*RequestDto`, `*ResponseDto`, `*PageResponseDto`
- Update `FEATURES.md` when adding/removing features

## Key dependencies

- `jjwt:0.12.6` for JWT
- `springdoc-openapi-starter-webmvc-ui:2.8.6` for Swagger UI
- Lombok for boilerplate

## Application properties

`app.jwt.secret` (Base64 HMAC key), `app.jwt.access-expiration` (86400000ms = 24h), `app.jwt.refresh-expiration` (604800000ms = 7d).
