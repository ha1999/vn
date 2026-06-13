# VN - Authentication & Authorization Service

A Spring Boot 4 REST API service providing JWT-based authentication, role-based access control, and dynamic endpoint permission management.

**Built with:** Java 21, Spring Boot 4.0.6, Spring Security, PostgreSQL, JWT (jjwt 0.12.6), Lombok, Springdoc OpenAPI

## Features

- **JWT Authentication** — Register, login, refresh tokens, get current user
- **Dynamic Authorization** — DB-driven endpoint permission rules (not hardcoded SecurityConfig)
- **Role Management** — Create/update/toggle roles with granular permission assignments
- **Permission Management** — Fine-grained permissions with CRUD endpoints
- **User Management** — Full user lifecycle (create, update, enable/disable, delete)
- **Swagger UI** — Auto-generated API docs with JWT Bearer auth scheme

## Quick Start

```bash
docker compose up -d          # start PostgreSQL
./gradlew compileJava         # compile
./gradlew test                # tests (uses H2, no PG needed)
```

### Default Credentials

| Username | Password | Role  |
|----------|----------|-------|
| admin    | admin123 | ADMIN |

## Architecture

```
auth.mix.vn
├── v1/auth/       — JWT auth, login/register/refresh, SecurityConfig
├── v1/user/       — User entity + management
├── v1/role/       — Role entity + management
├── v1/permission/ — Permission entity + constants + management
├── authorization/ — Dynamic endpoint permission guard (DB-driven)
├── common/        — BaseEntity, auditing, OpenApiConfig, error handling
└── seed/          — DataInitializer (auto-seeds on first run)
```

## Security Model

All HTTP requests pass through at the web level (`.anyRequest().permitAll()`). Authorization is enforced entirely by `EndpointPermissionFilter`, a custom `OncePerRequestFilter` that checks each request against the `endpoint_permissions` database table.

| Rule | Behavior |
|------|----------|
| No matching rule | 403 "No permission rule defined" |
| `enabled = false` | Allow |
| `requiredPermission = null` | Allow (public endpoint) |
| `requiredPermission` set | Check auth + user has that granted authority |

## API Documentation

Once running, visit: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Project Conventions

- No magic strings — use `PermissionName`, `RoleName` constants
- Thin controllers — all logic delegated to services
- DTO suffix — `*RequestDto`, `*ResponseDto`, `*PageResponseDto`
- Feature tracking — `FEATURES.md` maintained in sync with codebase

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | PostgreSQL (H2 for tests) |
| API Docs | Springdoc OpenAPI 2.8.6 |
| Build | Gradle |
| Boilerplate | Lombok |
