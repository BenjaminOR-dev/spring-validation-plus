# Spring Validation Plus — Example

[🇬🇧 English version](README.md) | [🇪🇸 Versión en español](README.es.md)

Referência **executável** de integração. Cada pacote e endpoint demonstra um padrão do [README principal](../README.pt.md).

Não é um app de produção: usa H2 em memória para que `@Unique` funcione sem configurar Oracle ou PostgreSQL.

## Como começar

Na raiz do repositório:

```bash
docker compose up example
```

A API fica em `http://localhost:8080`.

## Estrutura do código

```text
spring-validation-plus-example/
├── README.md / README.es.md / README.pt.md
├── pom.xml                            ← starter + web + JPA + H2
└── src/main/java/.../example/
    ├── ExampleApplication.java
    ├── dto/                           ← um DTO por padrão
    ├── entity/User.java
    ├── repository/UserRepository.java
    ├── service/UserService.java
    └── web/
        ├── UserController.java
        ├── OrderController.java
        └── DemoController.java
```

## Mapa DTO → padrão → endpoint

| DTO | Padrão | Método | Path |
|-----|--------|--------|------|
| `UserCreateRequest` | `@Unique` no create | `POST` | `/api/users` |
| `UserUpdateRequest` | `@Unique` + `excludeParameter`, `@Nullable` | `PUT` | `/api/users/{id}` |
| `UserSearchRequest` | `@Valid @ModelAttribute`, query params | `GET` | `/api/users` |
| — | `@Validated` + `@MinValue` no path | `GET` | `/api/users/{id}` |
| `OrderCreateRequest` | listas aninhadas + `@Valid` | `POST` | `/api/orders` |
| `ConditionalUserRequest` | `@RequiredIf` | `POST` | `/api/demo/conditional` |
| `PasswordRequest` | `@Same` | `POST` | `/api/demo/password` |

## Experimentar

```bash
# Create OK
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana","email":"ana@example.com"}'

# @Unique 400 (demo@example.com já existe em data.sql)
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Outro","email":"demo@example.com"}'

# @Same 400
curl -s -X POST http://localhost:8080/api/demo/password \
  -H "Content-Type: application/json" \
  -d '{"password":"secret","passwordConfirmation":"other"}'
```

Guia completa com todos os curls: [README.md](README.md) (EN) ou [README.es.md](README.es.md) (ES).
