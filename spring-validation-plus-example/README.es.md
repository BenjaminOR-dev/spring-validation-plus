# Spring Validation Plus — Example

[🇬🇧 English version](README.md)

Referencia **ejecutable** de integración. Cada paquete y endpoint demuestra un patrón del [README principal](../README.es.md).

No es una app de producción: usa H2 en memoria solo para que `@Unique` funcione sin configurar Oracle ni PostgreSQL.

## Arrancar

Desde la raíz del repositorio:

```bash
docker compose up example
```

La API queda en `http://localhost:8080`.

## Estructura del código

```text
spring-validation-plus-example/
├── README.es.md                       ← esta guía
├── pom.xml                            ← starter + web + JPA + H2
└── src/main/java/.../example/
    ├── ExampleApplication.java
    ├── dto/                           ← un DTO por patrón (ver tabla abajo)
    ├── entity/User.java               ← entidad JPA para @Unique
    ├── repository/UserRepository.java
    ├── service/UserService.java
    └── web/
        ├── UserController.java        ← CRUD + búsqueda
        ├── OrderController.java       ← listas anidadas
        └── DemoController.java        ← reglas condicionales
```

## Mapa DTO → patrón → endpoint

| DTO | Patrón que demuestra | Método | Ruta |
|-----|----------------------|--------|------|
| `UserCreateRequest` | `@Unique` en create | `POST` | `/api/users` |
| `UserUpdateRequest` | `@Unique` + `excludeParameter`, `@Nullable` password | `PUT` | `/api/users/{id}` |
| `UserSearchRequest` | `@Valid @ModelAttribute`, tipos en query params | `GET` | `/api/users` |
| — | `@Validated` + `@MinValue` en path variable | `GET` | `/api/users/{id}` |
| `OrderCreateRequest` | `@ArrayType`, `@Between`, `@Distinct`, `@Valid` anidado | `POST` | `/api/orders` |
| `ConditionalUserRequest` | Tipos Java + `@RequiredIf` | `POST` | `/api/demo/conditional` |
| `PasswordRequest` | `@Confirmed` | `POST` | `/api/demo/password` |

## Configuración (`application.properties`)

| Propiedad | Para qué sirve en el example |
|-----------|------------------------------|
| `spring.validation-plus.enabled=true` | Activa el validador con i18n |
| `spring.validation-plus.exception-handler.enabled=true` | Respuestas JSON `{ "errors": { ... } }` |
| `spring.web.locale=es` | Mensajes en español sin header |
| H2 + JPA | Registra `JpaUniquenessChecker` para `@Unique` |

## Probar cada patrón

### 1. Create + `@Unique`

```bash
# OK — email nuevo
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana","email":"ana@example.com"}'

# 400 — email duplicado (ya existe demo@example.com en data.sql)
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Otro","email":"demo@example.com"}'
```

### 2. Update + `excludeParameter` + password opcional

```bash
# OK — mismo usuario, mismo email (id=1)
curl -s -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Usuario Demo","email":"demo@example.com"}'

# 400 — password vacío "" (usar null u omitir el campo)
curl -s -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"X","email":"demo@example.com","password":""}'
```

### 3. Búsqueda — query params + DTO

```bash
# OK
curl -s "http://localhost:8080/api/users?page=0&size=10"

# 400 — size no numérico (error de conversión traducido)
curl -s "http://localhost:8080/api/users?size=abc"

# 400 — size fuera de rango (@MaxValue)
curl -s "http://localhost:8080/api/users?size=500"
```

### 4. Path variable validado

```bash
curl -s http://localhost:8080/api/users/1   # OK
curl -s http://localhost:8080/api/users/0   # 400 — @MinValue(1)
```

### 5. Lista anidada — `@Valid`

```bash
# OK
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Benja","items":[{"productName":"Libro","quantity":2}]}'

# 400 — quantity inválida en items[0]
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Benja","items":[{"productName":"Libro","quantity":0}]}'
```

### 6. `@RequiredIf`

```bash
# 400 — falta adminCode cuando role=ADMIN
curl -s -X POST http://localhost:8080/api/demo/conditional \
  -H "Content-Type: application/json" \
  -d '{"name":"Admin","email":"a@b.com","size":1,"role":"ADMIN"}'
```

### 7. `@Confirmed`

```bash
# 400 — confirmación no coincide
curl -s -X POST http://localhost:8080/api/demo/password \
  -H "Content-Type: application/json" \
  -d '{"password":"secret","passwordConfirmation":"other"}'
```

## Respuesta de error (formato común)

```json
{
  "errors": {
    "email": ["El email ya está registrado por otro usuario."]
  }
}
```

## Qué copiar a tu proyecto

1. Dependencia `spring-validation-plus-spring-boot-starter` en tu `pom.xml`.
2. Propiedades de `application.properties` (validación + locale).
3. El DTO que necesites del paquete `dto/` — cada clase tiene un comentario Javadoc con el patrón.
4. `@Valid` / `@Validated` en tu controller como en `web/`.
5. Para `@Unique`: añade JPA (o registra un `UniquenessChecker` custom).
