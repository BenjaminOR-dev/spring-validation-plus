# Spring Validation Plus — Example

[🇪🇸 Versión en español](README.es.md)

**Runnable** integration reference. Each package and endpoint demonstrates a pattern from the [main README](../README.md).

This is not a production app: it uses in-memory H2 so `@Unique` works without configuring Oracle or PostgreSQL.

## Getting started

From the repository root:

```bash
docker compose up example
```

The API is available at `http://localhost:8080`.

## Code structure

```text
spring-validation-plus-example/
├── README.md                          ← this guide
├── pom.xml                            ← starter + web + JPA + H2
└── src/main/java/.../example/
    ├── ExampleApplication.java
    ├── dto/                           ← one DTO per pattern (see table below)
    ├── entity/User.java               ← JPA entity for @Unique
    ├── repository/UserRepository.java
    ├── service/UserService.java
    └── web/
        ├── UserController.java        ← CRUD + search
        ├── OrderController.java       ← nested lists
        └── DemoController.java        ← conditional rules
```

## DTO → pattern → endpoint map

| DTO | Pattern demonstrated | Method | Path |
|-----|----------------------|--------|------|
| `UserCreateRequest` | `@Unique` on create | `POST` | `/api/users` |
| `UserUpdateRequest` | `@Unique` + `excludeParameter`, `@Nullable` password | `PUT` | `/api/users/{id}` |
| `UserSearchRequest` | `@Valid @ModelAttribute`, types in query params | `GET` | `/api/users` |
| — | `@Validated` + `@MinValue` on path variable | `GET` | `/api/users/{id}` |
| `OrderCreateRequest` | `@ArrayType`, `@Between`, `@Distinct`, nested `@Valid` | `POST` | `/api/orders` |
| `ConditionalUserRequest` | Java types + `@RequiredIf` | `POST` | `/api/demo/conditional` |
| `PasswordRequest` | `@Confirmed` | `POST` | `/api/demo/password` |

## Configuration (`application.properties`)

| Property | Purpose in the example |
|----------|------------------------|
| `spring.validation-plus.enabled=true` | Enables the validator with i18n |
| `spring.validation-plus.exception-handler.enabled=true` | JSON responses `{ "errors": { ... } }` |
| `spring.web.locale=es` | Spanish messages without an `Accept-Language` header |
| H2 + JPA | Registers `JpaUniquenessChecker` for `@Unique` |

## Try each pattern

### 1. Create + `@Unique`

```bash
# OK — new email
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana","email":"ana@example.com"}'

# 400 — duplicate email (demo@example.com already exists in data.sql)
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Otro","email":"demo@example.com"}'
```

### 2. Update + `excludeParameter` + optional password

```bash
# OK — same user, same email (id=1)
curl -s -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Usuario Demo","email":"demo@example.com"}'

# 400 — empty password "" (use null or omit the field)
curl -s -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"X","email":"demo@example.com","password":""}'
```

### 3. Search — query params + DTO

```bash
# OK
curl -s "http://localhost:8080/api/users?page=0&size=10"

# 400 — size not numeric (translated conversion error)
curl -s "http://localhost:8080/api/users?size=abc"

# 400 — size out of range (@MaxValue)
curl -s "http://localhost:8080/api/users?size=500"
```

### 4. Validated path variable

```bash
curl -s http://localhost:8080/api/users/1   # OK
curl -s http://localhost:8080/api/users/0   # 400 — @MinValue(1)
```

### 5. Nested list — `@Valid`

```bash
# OK
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Benja","items":[{"productName":"Libro","quantity":2}]}'

# 400 — invalid quantity in items[0]
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Benja","items":[{"productName":"Libro","quantity":0}]}'
```

### 6. `@RequiredIf`

```bash
# 400 — missing adminCode when role=ADMIN
curl -s -X POST http://localhost:8080/api/demo/conditional \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","email":"a@b.com","size":1,"role":"ADMIN"}'
```

### 7. `@Confirmed`

```bash
# 400 — confirmation does not match
curl -s -X POST http://localhost:8080/api/demo/password \
  -H "Content-Type: application/json" \
  -d '{"password":"secret","passwordConfirmation":"other"}'
```

## Error response (common format)

The example sets `spring.web.locale=es`, so validation messages are returned in Spanish at runtime.

```json
{
  "errors": {
    "email": ["El email ya está registrado por otro usuario."]
  }
}
```

## What to copy into your project

1. Add the `spring-validation-plus-spring-boot-starter` dependency to your `pom.xml`.
2. Copy the relevant `application.properties` settings (validation + locale).
3. Copy the DTO you need from the `dto/` package — each class has a Javadoc comment describing the pattern.
4. Use `@Valid` / `@Validated` on your controller as in `web/`.
5. For `@Unique`: add JPA (or register a custom `UniquenessChecker`).
