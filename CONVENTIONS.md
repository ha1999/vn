# Project Conventions

This file documents the conventions that must be followed when writing code for this project.

## 1. No Magic Strings or Magic Numbers

- Do not use hardcoded string literals or numeric literals directly in business logic.
- Use `enum`, `static final` constants, or `application.properties` instead.
- **Bad:** `if (role.equals("ADMIN"))`
- **Good:** `if (role.equals(RoleName.ADMIN.name()))` or `if (RoleName.ADMIN.equals(role))`

## 2. Meaningful Variable Names

- All variables, methods, classes, and packages must have descriptive, self-documenting names.
- Do not use single-letter names (`a`, `b`, `c`, `x`, `y`) except for loop counters in trivial contexts.
- **Bad:** `User u = new User();`
- **Good:** `User user = new User();`

## 3. Controller Layer Is Thin

- Controllers must **only** handle HTTP request/response mapping and delegate to the service layer.
- No business logic, no conditionals beyond error handling.
- Controllers must not call repositories, entities, or security components directly.
- **Bad:** Controller calling `userRepository.save(...)` or parsing JWT.
- **Good:** Controller calls `authService.register(request)` and returns the response.

## 4. DTO Class Naming

- All Data Transfer Objects must have the `Dto` suffix in the class name.
- **Bad:** `LoginRequest`, `JwtResponse`
- **Good:** `LoginRequestDto`, `JwtResponseDto`

## 5. Feature Tracking

- After every update, creation, or deletion of a feature, update `FEATURES.md`.
- This file must contain a list of all application features with a brief description and status.
- Keep it in sync with the actual state of the codebase.
