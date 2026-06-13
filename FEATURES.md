# Application Features

## V1 API (`v1/`) — all versioned API modules live under `auth.mix.vn.v1`

### Authentication (`v1/auth/`) — sub-packages: `controller`, `service`, `dto`, `config`

| Feature | Description | Status |
|---|---|---|
| Register | Create a new user account with USER role | Done |
| Login | Authenticate with username/email and password, receive JWT | Done |
| Logout | Log user logout event to `login_logs` table | Done |
| Login Audit | Log all login success/failure events with IP and user-agent | Done |
| Refresh Token | Exchange a refresh token for a new access token | Done |
| Get Current User | Return authenticated user's profile and permissions | Done |
| JWT Provider (config) | Generate/validate access and refresh tokens via jjwt | Done |
| JWT Auth Filter (config) | Extract JWT from Authorization header and set security context | Done |
| Auth Entry Point (config) | Return 401 for unauthenticated requests | Done |
| Security Config (config) | Spring Security filter chain, CORS, session management | Done |
| Auth Constants (config) | `AuthConstants.BEARER_PREFIX`, `AUTHORIZATION_HEADER` | Done |
| CustomUserDetails (config) | UserDetails implementation with flattened permissions | Done |

## OpenAPI / Swagger (`common/`)

| Feature | Description | Status |
|---|---|---|
| OpenAPI Config | `OpenApiConfig` with JWT Bearer security scheme, app info | Done |
| Swagger UI | `/swagger-ui.html` via springdoc-openapi | Done |
| API Docs | `/api-docs` JSON endpoint | Done |

## Authorization (`authorization/`)

| Feature | Description | Status |
|---|---|---|
| Dynamic Endpoint Guard | Protect endpoints via DB rules (method + path pattern → required permission) | Done |
| Path Matching | Match incoming requests to the most specific rule | Done |
| Disable/Enable Rules | Toggle endpoint protection on/off without restart | Done |
| Public Endpoints | Set `requiredPermission = null` to make an endpoint public | Done |
| Authorization Constants | `AuthorizationConstants.METHOD_ANY`, `PUBLIC_API_PATTERN` | Done |

## Seed Data (`seed/`)

| Feature | Description | Status |
|---|---|---|
| Default Admin | admin / admin123 with ADMIN role | Done |
| Default Roles | ADMIN, MODERATOR, USER with appropriate permissions | Done |
| Default Endpoint Rules | 19 pre-defined rules for api paths | Done |
| ADMIN role | All permissions including user:disable, role:disable, permission:* | Done |

### User Module (`v1/user/`) — sub-packages: `entity`, `controller`, `service`, `dto`, `repository`

| Feature | Description | Status |
|---|---|---|
| User Entity (entity) | JPA entity with username, email, password, displayName, enabled, roles | Done |
| List Users | Paginated user list via `UserController.listUsers()` | Done |
| Get User Details | Return user profile via `UserController.getUserDetails()` | Done |
| Create User | Admin creates a user via `UserController.createUser()` | Done |
| Update User | Update email, displayName, roles via `UserController.updateUser()` | Done |
| Toggle User Status | Enable/disable user via `UserController.toggleUserStatus()` | Done |
| Delete User | Remove user via `UserController.deleteUser()` | Done |

### Role Module (`v1/role/`) — sub-packages: `entity`, `config`, `controller`, `service`, `dto`, `repository`

| Feature | Description | Status |
|---|---|---|
| Role Entity (entity) | JPA entity with name, description, enabled, permissions | Done |
| RoleName Enum (config) | `RoleName` enum (ADMIN, MODERATOR, USER) | Done |
| List Roles | Paginated role list via `RoleController.listRoles()` | Done |
| Get Role Detail | Return role with permission names via `RoleController.getRole()` | Done |
| Create Role | Create role with assigned permissions via `RoleController.createRole()` | Done |
| Update Role | Update name, description, permissions via `RoleController.updateRole()` | Done |
| Toggle Role Status | Enable/disable role via `RoleController.toggleRoleStatus()` | Done |

### Permission Module (`v1/permission/`) — sub-packages: `entity`, `config`, `controller`, `service`, `dto`, `repository`

| Feature | Description | Status |
|---|---|---|
| Permission Entity (entity) | JPA entity with name, enabled | Done |
| PermissionName Constants (config) | All permission string constants (user:*, role:*, permission:*, admin:access, endpoint-permission:*) | Done |
| List Permissions | Paginated list via `PermissionController.listPermissions()` | Done |
| Get Permission Detail | Return permission by ID via `PermissionController.getPermission()` | Done |
| Create Permission | Create a new permission via `PermissionController.createPermission()` | Done |
| Update Permission | Update permission name via `PermissionController.updatePermission()` | Done |
| Toggle Permission Status | Enable/disable permission via `PermissionController.togglePermissionStatus()` | Done |

## Conventions (`CONVENTIONS.md`)

| Feature | Description | Status |
|---|---|---|
| No Magic Strings | All string/number literals use constants or enums | Done |
| Meaningful Names | All variables use descriptive names | Done |
| Thin Controller | Controller delegates all logic to service layer | Done |
| DTO Suffix | All DTO classes use `Dto` suffix | Done |
| Feature Tracking | `FEATURES.md` kept in sync with codebase | Done |
