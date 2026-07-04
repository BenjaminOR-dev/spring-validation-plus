# Spring Validation Plus

[🇬🇧 English version](README.md) | [🇧🇷 Versão em português](README.pt.md)

[![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%7C%204.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.x-blue)](https://jakarta.ee/specifications/bean-validation/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter?label=Maven%20Central)](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Validaciones inspiradas en **Laravel** para **Spring Boot** y **Jakarta Validation**.

Spring Validation Plus añade más de **85 constraints** personalizados que funcionan como los nativos de Jakarta Validation: los anotas en tus DTOs y se ejecutan con `@Valid` o `@Validated`. Incluye mensajes i18n (es/en), manejo unificado de errores JSON y soporte opcional para reglas de base de datos (`@Unique`, `@Exists`).

**Incluye:**

- Constraints estilo Laravel (`@Required`, `@EmailAddress`, `@Confirmed`, `@RequiredIf`, …)
- Validación de tipos (`@StringType`, `@IntegerType`, `@ArrayType`, …)
- Respuestas JSON `{ "errors": { "campo": ["mensaje"] } }`
- Traducción automática de errores de conversión (query params, JSON mal tipado)
- Integración JPA opcional para `@Unique` y `@Exists`
- Core usable sin Spring Boot (`spring-validation-plus-core`)

## Tabla de contenidos

- [Requisitos](#requisitos)
- [Inicio rápido](#inicio-rápido)
- [Configuración](#configuración)
- [Guía de uso](#guía-de-uso)
  - [Patrón recomendado por campo](#patrón-recomendado-por-campo)
  - [Campos opcionales (`@Nullable`)](#campos-opcionales-nullable)
  - [Body JSON (`@RequestBody`)](#body-json-requestbody)
  - [Query params y formularios (`@ModelAttribute`)](#query-params-y-formularios-modelattribute)
  - [Path variables (`@Validated`)](#path-variables-validated)
  - [Arrays y listas](#arrays-y-listas)
  - [Validación anidada con DTOs](#validación-anidada-con-dtos)
  - [Reglas a nivel clase](#reglas-a-nivel-clase)
  - [Base de datos (`@Unique`, `@Exists`)](#base-de-datos-unique-exists)
- [Respuesta de errores](#respuesta-de-errores)
- [Internacionalización (i18n)](#internacionalización-i18n)
- [Handler de excepciones](#handler-de-excepciones)
- [Arquitectura de módulos](#arquitectura-de-módulos)
- [Referencia ejecutable (example)](#referencia-ejecutable-example)
- [Solución de problemas](#solución-de-problemas)
- [Referencia de constraints](#referencia-de-constraints)
- [Desarrollo](#desarrollo)
- [Roadmap](#roadmap)
- [Licencia](#licencia)

## Requisitos

- Java 17+
- Spring Boot 3.x / 4.x
- Jakarta Validation 3.x

## Inicio rápido

### 1. Dependencia

**Maven**

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

**Gradle (Kotlin DSL)**

```kotlin
implementation("io.github.benjaminor-dev:spring-validation-plus-spring-boot-starter:0.1.0")
```

**Gradle (Groovy)**

```groovy
implementation 'io.github.benjaminor-dev:spring-validation-plus-spring-boot-starter:0.1.0'
```

**Multi-módulo Maven** (mismo repositorio):

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>${project.version}</version>
</dependency>
```

> Disponible en [Maven Central](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter) — no hace falta configurar repositorios extra.

**Dependencias opcionales**

| Necesitas | Añade también |
|-----------|---------------|
| Solo validación (sin Spring Boot) | `spring-validation-plus-core` |
| Reglas `@Unique` / `@Exists` con JPA | `spring-boot-starter-data-jpa` |

### 2. Anota tu DTO

```java
public class UserCreateRequest {

    @Required
    @StringType
    @MinLength(2)
    @MaxLength(50)
    private String name;

    @Required
    @StringType
    @EmailAddress
    @MaxLength(255)
    private String email;

    @Required
    @StringType
    @MinLength(6)
    @MaxLength(255)
    private String password;
}
```

### 3. Valida en el controller

```java
@PostMapping("/users")
public ResponseEntity<User> create(@Valid @RequestBody UserCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
}
```

El starter configura automáticamente el `Validator` con i18n y, si está habilitado, un `ControllerAdvice` que devuelve errores en formato JSON unificado.

> **Nota:** tu proyecto debe incluir validación Jakarta (`spring-boot-starter-validation` o `spring-boot-starter-web`, que ya la trae transitivamente).

## Configuración

Propiedades disponibles en `application.properties`:

```properties
# Activa/desactiva la integración (default: true)
spring.validation-plus.enabled=true

# Activa el ValidationExceptionHandler incluido (default: true)
spring.validation-plus.exception-handler.enabled=true

# Idioma por defecto cuando no hay header Accept-Language
spring.web.locale=es
spring.web.locale-resolver=accept_header
```

| Propiedad | Default | Descripción |
|-----------|---------|-------------|
| `spring.validation-plus.enabled` | `true` | Validador, integración Spring y checkers JPA (`@Unique` / `@Exists`) |
| `spring.validation-plus.exception-handler.enabled` | `true` | `ValidationExceptionHandler` para errores 400 en JSON |

Si tu app ya tiene un `@RestControllerAdvice` propio para validación, desactiva el handler de la librería y delega solo los errores de negocio (404, 401, etc.) a tu advice local.

## Guía de uso

### Patrón recomendado por campo

Apila las anotaciones en este orden:

1. **Presencia** — `@Required` o `@Nullable`
2. **Tipo** — `@StringType`, `@IntegerType`, `@ArrayType`, etc.
3. **Reglas de negocio** — `@MinLength`, `@MinValue`, `@EmailAddress`, etc.

```java
@Required          // 1. obligatorio
@StringType        // 2. debe ser String
@MinLength(2)      // 3. al menos 2 caracteres
@MaxLength(50)     // 3. máximo 50 caracteres
private String name;

@IntegerType       // 2. debe ser entero Java
@MinValue(0)        // 3. >= 0
@MaxValue(100)     // 3. <= 100
private Integer page;
```

| Tipo Java | Constraints de tipo | Constraints de rango / formato |
|-----------|---------------------|--------------------------------|
| `String` | `@StringType` | `@MinLength`, `@MaxLength`, `@EmailAddress`, `@Regex`, `@In`, … |
| `Integer`, `Long`, … | `@IntegerType` | `@MinValue`, `@MaxValue`, `@Between`, `@Gt`, `@Lt`, … |
| `Double`, `Float`, `BigDecimal` | `@DecimalType` | `@MinValue`, `@MaxValue`, `@Digits`, … |
| `Boolean` | `@BooleanType` | `@Accepted`, `@Declined` |
| `List`, `Set`, array | `@ArrayType` | `@Between` (tamaño), `@Size`, `@Distinct` |
| Fechas (`LocalDate`, `String`) | `@Date`, `@DateFormat` | `@Before`, `@After`, `@BeforeOrEqual`, … |

> `@EmailAddress`, `@MinLength` y similares **ignoran valores null o en blanco**. Combínalos con `@Required` cuando el campo sea obligatorio.

### Campos opcionales (`@Nullable`)

Para campos que **no son obligatorios** en updates parciales (contraseña, apellido, etc.):

```java
@Nullable
@StringType
@MinLength(6)
@MaxLength(255)
private String password;
```

| Valor en JSON | `@Nullable` | `@MinLength(6)` | Resultado |
|---------------|-------------|-----------------|-----------|
| Campo **omitido** | ✅ | — (null) | ✅ Pasa |
| `"password": null` | ✅ | — (null) | ✅ Pasa |
| `"password": ""` | ✅ | ❌ longitud 0 | **400** — debe tener al menos 6 caracteres |
| `"password": "abc"` | ✅ | ❌ | **400** |
| `"password": "secret123"` | ✅ | ✅ | ✅ Pasa |

**Regla práctica:** si el cliente **no quiere cambiar** el campo, debe **omitirlo** o enviar `null`. Un string vacío `""` cuenta como valor presente y las reglas de longitud/formato sí aplican.

Lo mismo aplica a filtros opcionales en query params: `@Nullable` + `@EmailAddress` permite dejar `email` vacío en la URL.

### Body JSON (`@RequestBody`)

Flujo estándar para POST/PUT:

```
JSON → Jackson deserializa → @Valid ejecuta constraints → controller
```

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody UserCreateRequest request) { ... }
```

Si el JSON trae un tipo incorrecto (`"size": "abc"`), el `ValidationExceptionHandler` traduce el error de Jackson a un mensaje i18n amigable.

### Query params y formularios (`@ModelAttribute`)

Para GET con filtros, paginación o **POST** con `application/x-www-form-urlencoded`:

```java
@GetMapping
public ResponseEntity<List<User>> search(@Valid @ModelAttribute UserSearchRequest request) { ... }

@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
public ResponseEntity<?> createForm(@Valid @ModelAttribute UserCreateRequest request) { ... }
```

```java
public class UserSearchRequest {

    @Nullable
    @StringType
    @MaxLength(50)
    private String name;

    @IntegerType
    @MinValue(0)
    private Integer page = 0;

    @IntegerType
    @MinValue(1)
    @MaxValue(100)
    private Integer size = 10;
}
```

**Importante:** Spring convierte los query params **antes** de ejecutar `@Valid`.

| Escenario | Qué falla | Quién responde |
|-----------|-----------|----------------|
| `?size=abc` (no numérico) | Conversión de tipo | `ValidationExceptionHandler` → `"El campo size debe ser un entero."` |
| `?size=0` (numérico inválido) | `@MinValue(1)` | Constraint de validación |
| `?email=foo` (email malo) | `@EmailAddress` | Constraint de validación |

`@IntegerType` en un campo `Integer` **no intercepta** texto no numérico en query params; eso ocurre en la fase de binding. Una vez convertido correctamente, `@IntegerType` y `@MinValue` sí aplican.

### Path variables (`@Validated`)

Para validar parámetros de ruta o de método (no DTOs), anota el controller con `@Validated` y usa constraints en el parámetro:

```java
@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public User find(@PathVariable @MinValue(1) Long id) { ... }
}
```

Los errores se devuelven como `{ "errors": { "id": ["..."] } }` con mensajes i18n (mismo formato que en el body).

### Arrays y listas

Hay **dos niveles** de validación:

| Nivel | Qué validas | Constraints |
|-------|-------------|-------------|
| El array en sí | Tipo, cantidad de elementos, sin duplicados | `@ArrayType`, `@Between`, `@Size`, `@Distinct`, `@Required` |
| Cada elemento | Email, longitud, campos propios | **DTO hijo + `@Valid`** (ver siguiente sección) |

**Lista de valores simples** (tags, IDs):

```java
public class BulkTagRequest {

    @Required
    @ArrayType
    @Between(min = 1, max = 20)   // entre 1 y 20 elementos
    @Distinct                     // sin repetidos
    private List<String> tags;
}
```

`@Between` funciona sobre números, longitud de strings **y tamaño de colecciones/arreglos**.

### Validación anidada con DTOs

Cuando cada item del array es un objeto con reglas propias, usa un DTO hijo y `@Valid` de Jakarta:

```java
public class OrderItemRequest {

    @Required
    @StringType
    @MaxLength(100)
    private String productName;

    @Required
    @IntegerType
    @MinValue(1)
    private Integer quantity;
}

public class CreateOrderRequest {

    @Required
    @ArrayType
    @Between(min = 1, max = 50)
    @Valid                                    // ← valida cada OrderItemRequest
    private List<OrderItemRequest> items;
}
```

Errores anidados en la respuesta:

```json
{
  "errors": {
    "items[0].quantity": ["El campo quantity debe ser al menos 1.0."],
    "items": ["El campo items debe tener entre 1.0 y 50.0 elementos."]
  }
}
```

> Los constraints de validation-plus **no soportan aún** anotaciones inline en generics (`List<@EmailAddress String>`). Para validar cada string de una lista, usa un DTO wrapper o un DTO hijo con `@Valid`.

### Reglas a nivel clase

Constraints que relacionan varios campos del mismo DTO:

```java
@RequiredIf(field = "role", value = "ADMIN", required = "adminCode")
@Confirmed(field = "password")
public class UserRequest {

    private String role;
    private String adminCode;
    private String password;
    private String passwordConfirmation;
}
```

Ver [Referencia de constraints → Entre campos](#entre-campos-nivel-clase).

### Base de datos (`@Unique`, `@Exists`)

Validaciones que consultan persistencia en runtime. Requieren un **checker** registrado vía SPI.

#### Checklist de integración JPA

1. Dependencia `spring-boot-starter-data-jpa` en tu proyecto (además del starter de validation-plus).
2. Entidad JPA con `@Entity` (p. ej. `User.class`).
3. Constraint `@Unique` o `@Exists` a **nivel clase** del DTO, apuntando al campo del DTO y al atributo JPA.
4. En **updates**, usar `excludeParameter` o `excludeField` para no comparar contra el propio registro.

El starter registra automáticamente `JpaUniquenessChecker` y `JpaExistenceChecker` cuando detecta un `EntityManagerFactory` (después de la auto-config de Hibernate). Las consultas de base de datos se ejecutan en transacción de solo lectura, también con `spring.jpa.open-in-view=false`.

#### Checkers custom (Spring Boot)

Declara un bean en lugar de los defaults JPA — el starter lo registra automáticamente:

```java
@Bean
UniquenessChecker uniquenessChecker(UserRepository repository) {
    return request -> repository.countByEmail((String) request.value()) == 0;
}
```

Si existe un bean `UniquenessChecker` o `ExistenceChecker`, no se crea la implementación JPA.

#### Parámetros de `@Unique`

| Parámetro | Descripción |
|-----------|-------------|
| `entity` | Clase `@Entity` consultada |
| `field` | Campo del **DTO** validado y donde se reporta el error |
| `column` | **Atributo JPA** usado en JPQL (`e.email`, no necesariamente el nombre físico de columna SQL) |
| `excludeField` | Campo del DTO con el id a excluir (alternativa a `excludeParameter`) |
| `excludeParameter` | Nombre del `@PathVariable` o query param con el id (p. ej. `"id"` en `PUT /users/{id}`) |
| `excludeColumn` | Atributo identificador de la entidad (default: `"id"`) |
| `ignoreCase` | Comparación case-insensitive para strings (default: `true`) |
| `message` | Mensaje personalizado (soporta i18n con claves `{...}`) |

#### Crear — valor único

```java
@Unique(entity = User.class, field = "email", column = "email")
public class UserCreateRequest {

    @Required
    @StringType
    @EmailAddress
    @MaxLength(255)
    private String email;
}
```

#### Actualizar — excluir el registro actual

`excludeParameter` lee el id desde la petición HTTP actual (path variable) mediante `RequestContextValueProvider`:

```java
@Unique(
    entity = User.class,
    field = "email",
    column = "email",
    excludeParameter = "id",
    message = "El email ya se encuentra registrado por otro usuario."
)
public class UserUpdateRequest {

    @Required
    @EmailAddress
    private String email;
}
```

```java
@PutMapping("/{id}")
public User update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) { ... }
//                                    ↑ debe coincidir con excludeParameter = "id"
```

#### `@Exists` — el registro debe existir

```java
@Exists(entity = Role.class, field = "roleId", column = "id")
public class AssignRoleRequest {

    @Required
    @IntegerType
    private Long roleId;
}
```

#### Backend sin JPA (SPI manual)

Fuera de Spring Boot, o si prefieres registro explícito, usa el registro SPI al arrancar:

```java
ValidationPlusCheckers.registerUniquenessChecker(request -> {
    // return true si el valor es único
});
ValidationPlusCheckers.registerExistenceChecker(request -> {
    // return true si el registro existe
});
```

Para valores de contexto HTTP (path variables), registra un `ContextValueProvider`. En apps web Spring Boot se incluye `RequestContextValueProvider` por defecto; puedes reemplazarlo con tu propio `@Bean ContextValueProvider`.

#### Errores frecuentes de `@Unique`

| Mensaje | Causa | Solución |
|---------|-------|----------|
| `No hay un verificador de unicidad configurado para {field}` | No hay checker registrado | Añade `spring-boot-starter-data-jpa` o registra un `UniquenessChecker` manual |
| El email existe pero es el **mismo** registro en update | Falta excluir el id actual | Usa `excludeParameter = "id"` alineado con `@PathVariable` |
| Error JPA / entidad no encontrada | `entity` o `column` incorrectos | `column` = nombre del **atributo** en la entidad Java |

## Respuesta de errores

Formato JSON estilo Laravel, devuelto por `ValidationExceptionHandler`:

```json
{
  "errors": {
    "email": ["El campo email es obligatorio."],
    "size": ["El campo size debe ser un entero."]
  }
}
```

El handler cubre:

- `MethodArgumentNotValidException` / `BindException` — body JSON, query params y campos de formulario
- `MethodArgumentTypeMismatchException` — path o query params con tipos incompatibles
- `HttpMessageNotReadableException` — JSON malformado o tipos incorrectos en el body
- `HandlerMethodValidationException` / `ConstraintViolationException` — `@Validated` en métodos del controller (path variables, parámetros de método)

Los errores de conversión (`typeMismatch`) se traducen a i18n con `FieldErrorMessageResolver` y `TypeMismatchMessageUtils` (`dev.benjaminor.validationplus.type.integer`, etc.).

## Internacionalización (i18n)

Mensajes incluidos en el core:

| Archivo | Idioma |
|---------|--------|
| `ValidationMessages.properties` | Inglés (default) |
| `ValidationMessages_en.properties` | Inglés |
| `ValidationMessages_es.properties` | Español |
| `ValidationMessages_pt.properties` | Portugués |

**Selección de idioma:**

1. Header `Accept-Language: es`, `Accept-Language: pt` o `Accept-Language: en`
2. Fallback: `spring.web.locale=es` (Spring Boot)

**Sobrescribir mensajes** en tu app — crea `src/main/resources/ValidationMessages_es.properties`:

```properties
dev.benjaminor.validationplus.constraints.Required.message=El campo {field} es requerido.
```

**Placeholders disponibles:** `{field}`, `{min}`, `{max}`, `{value}`, `{other}`, `{validatedValue}`, `{integer}`, `{fraction}`

## Handler de excepciones

Por defecto se registra `ValidationExceptionHandler` como `@RestControllerAdvice`. Para usar solo el validador y tu propio advice:

```properties
spring.validation-plus.exception-handler.enabled=false
```

Tu advice debe manejar al menos `MethodArgumentNotValidException` y `BindException` si quieres respuestas 400 personalizadas. Los errores de conversión de query params (`typeMismatch` en `FieldError`) los traduce internamente `FieldErrorMessageResolver` cuando el handler de la librería está activo.

## Arquitectura de módulos

```text
spring-validation-plus/
├── spring-validation-plus-core/                 # Publicable. Sin Spring Boot.
│   ├── constraints/                             # @Required, @EmailAddress, @Unique, …
│   ├── validators/                              # Implementaciones Jakarta Validation
│   ├── support/                                 # i18n, utilidades
│   ├── spi/                                     # UniquenessChecker, ExistenceChecker, …
│   └── resources/ValidationMessages*.properties
│
├── spring-validation-plus-spring-boot-starter/  # Publicable. Auto-config Spring Boot.
│   ├── autoconfigure/                           # Validator, JPA checkers, locale
│   ├── exception/                               # ValidationExceptionHandler
│   └── jpa/                                     # JpaUniquenessChecker, JpaExistenceChecker
│
└── spring-validation-plus-example/              # No publicable. Referencia ejecutable.
    └── README.md                                # Mapa DTO → patrón → curl
```

| Artefacto Maven | Cuándo usarlo |
|-----------------|---------------|
| `spring-validation-plus-spring-boot-starter` | Apps Spring Boot (recomendado) |
| `spring-validation-plus-core` | Proyectos Jakarta Validation sin Spring, o frameworks custom |

**Auto-configuración incluida en el starter:**

| Clase | Responsabilidad |
|-------|-----------------|
| `SpringValidationPlusAutoConfiguration` | `LocalValidatorFactoryBean` con i18n, `ValidationExceptionHandler`, `RequestContextValueProvider` |
| `JpaValidationPlusAutoConfiguration` | `JpaUniquenessChecker` + `JpaExistenceChecker` (si hay JPA) |

## Referencia ejecutable (example)

El módulo **`spring-validation-plus-example`** es documentación **viva**: código runnable + guía con curls.

```bash
docker compose up example   # http://localhost:8080
```

Consulta **[spring-validation-plus-example/README.es.md](spring-validation-plus-example/README.es.md)** — incluye:

- Mapa **DTO → patrón → endpoint**
- Ejemplos de `@Unique`, `@ModelAttribute`, `@Valid` anidado, `@RequiredIf`, `@Confirmed`
- H2 en memoria para probar reglas de base de datos sin instalar nada extra

## Solución de problemas

### Los mensajes salen en inglés

Postman y muchos clientes **no envían** `Accept-Language`. Configura idioma por defecto:

```properties
spring.web.locale=es
spring.web.locale-resolver=accept_header
```

O envía el header `Accept-Language: es` en cada petición.

### Query param numérico inválido muestra error en inglés

Activa el handler de la librería:

```properties
spring.validation-plus.exception-handler.enabled=true
```

Si usas tu propio `@RestControllerAdvice`, debes traducir errores `typeMismatch` en `FieldError` o reutilizar la lógica de `FieldErrorMessageResolver`.

### `@Unique` responde "No hay un verificador de unicidad configurado"

1. Confirma `spring-boot-starter-data-jpa` en el classpath.
2. Confirma que la app arranca con JPA (DataSource + entidades).
3. Usa versión del starter que incluye `JpaValidationPlusAutoConfiguration` (se activa **después** de Hibernate).
4. Sin JPA: implementa y registra `UniquenessChecker` manualmente (ver [SPI custom](#backend-sin-jpa-spi-custom)).

### `@MinLength` falla en un campo que quiero dejar vacío

`""` **no es** lo mismo que `null`. Omite el campo del JSON o envía `"campo": null`. Ver [Campos opcionales](#campos-opcionales-nullable).

### Validación anidada no corre en una lista

Falta `@Valid` de Jakarta en la colección:

```java
@Valid
private List<ItemRequest> items;
```

### Quiero desactivar solo el handler, no el validador

```properties
spring.validation-plus.exception-handler.enabled=false
spring.validation-plus.enabled=true
```

## Referencia de constraints

### Campo

| Constraint | Descripción |
|---|---|
| `@Required` | Campo obligatorio (no null, no vacío) |
| `@Nullable` | Documenta que puede ser null; nunca falla |
| `@Filled` | No vacío si está presente |
| `@StringType` | Debe ser `String` / `CharSequence` |
| `@IntegerType` | Debe ser entero Java (`Integer`, `Long`, `Byte`, …) |
| `@DecimalType` | `Float`, `Double` o `BigDecimal` |
| `@BooleanType` | `Boolean` real |
| `@ArrayType` | Array o `Collection` |
| `@Numeric` | Cualquier `Number` |
| `@EmailAddress` | Email válido (ignora blank) |
| `@MinLength` / `@MaxLength` | Longitud mínima/máxima |
| `@MinValue` / `@MaxValue` | Valor numérico mínimo/máximo |
| `@Between` | Rango numérico, longitud de texto o tamaño de colección |
| `@Size` | Tamaño exacto (texto, colección o número) |
| `@Accepted` / `@Declined` | Valores truthy/falsy: `true`/`false`, `yes`/`no`, `on`/`off`, `1`/`0`, strings `true`/`false`, `T`/`F` (sin distinguir mayúsculas) |
| `@In` / `@NotIn` | Valor dentro/fuera de una lista |
| `@Regex` / `@NotRegex` | Coincide/no coincide con patrón |
| `@Url` | URL válida |
| `@ActiveUrl` | URL http(s) con host (sin DNS/HTTP) |
| `@Uuid` | UUID válido |
| `@Ulid` | ULID válido |
| `@HexColor` | Color hexadecimal |
| `@Date` | Fecha válida (temporal o string) |
| `@DateFormat` | Fecha con formato obligatorio |
| `@Before` / `@After` | Fecha anterior/posterior a otra |
| `@BeforeOrEqual` / `@AfterOrEqual` | Fecha anterior/posterior inclusive |
| `@Digits` | Dígitos enteros y decimales exactos |
| `@MinDigits` / `@MaxDigits` / `@DigitsBetween` | Cantidad de dígitos |
| `@StartsWith` / `@EndsWith` | Prefijo/sufijo de texto |
| `@NotStartsWith` / `@NotEndsWith` | No empieza/termina con texto |
| `@Json` | Cadena JSON válida |
| `@Alpha` / `@AlphaNum` / `@AlphaDash` | Solo letras, alfanumérico, alfanumérico con guiones |
| `@Ascii` | Solo caracteres ASCII |
| `@Lowercase` / `@Uppercase` | Minúsculas / mayúsculas |
| `@Ip` / `@Ipv4` / `@Ipv6` / `@MacAddress` | Direcciones de red |
| `@Timezone` | Zona horaria válida |
| `@Gt` / `@Gte` / `@Lt` / `@Lte` | Comparaciones numéricas estrictas |
| `@MultipleOf` | Múltiplo de un número |
| `@EnumValue` | Valor de un enum Java |
| `@Password` | Política de contraseña configurable (`min`, `letters`, `numbers`, …) |
| `@Distinct` | Valores únicos en array/colección |
| `@File` | Archivo subido (`MultipartFile`, `Part`) con tamaño/tipo opcional |
| `@Image` | Imagen subida con dimensiones y tamaño opcionales |

### Entre campos (nivel clase)

| Constraint | Descripción |
|---|---|
| `@RequiredIf` | Obligatorio si otro campo tiene un valor |
| `@RequiredUnless` | Obligatorio salvo que otro campo tenga un valor |
| `@RequiredWith` / `@RequiredWithout` | Obligatorio si algún companion está presente/ausente |
| `@RequiredWithAll` / `@RequiredWithoutAll` | Obligatorio si todos/ausencia total de companions |
| `@RequiredIfAccepted` / `@RequiredIfDeclined` | Obligatorio si campo aceptado/rechazado |
| `@Same` / `@Different` | Dos campos deben coincidir / ser distintos |
| `@Confirmed` | Debe coincidir con `{field}Confirmation` |
| `@Prohibited` / `@ProhibitedIf` / `@ProhibitedUnless` | Campo prohibido (condicional) |
| `@Missing` / `@MissingIf` / `@MissingUnless` | Campo debe estar ausente (condicional) |
| `@MissingWith` / `@MissingWithAll` | Ausente si companion(s) presente(s) |
| `@InArray` | Valor debe existir en otro campo array/colección |

### Base de datos (nivel clase, SPI)

| Constraint | Descripción |
|---|---|
| `@Unique` | Valor único en tabla/entidad |
| `@Exists` | Registro debe existir en tabla/entidad |

> `@Unique` y `@Exists` son `@Repeatable`: puedes declarar varias reglas en el mismo DTO.

## Desarrollo

```text
spring-validation-plus/
├── spring-validation-plus-core/
├── spring-validation-plus-spring-boot-starter/
└── spring-validation-plus-example/    ← referencia ejecutable (ver README.es.md)
```

```bash
# Compilar y ejecutar todos los tests
docker compose run --rm maven

# Solo tests del core
docker compose run --rm maven mvn -pl spring-validation-plus-core test

# App de referencia (puerto 8080)
docker compose up example

# Instalar en .m2 local (p. ej. para probar un SNAPSHOT de main)
docker compose run --rm maven mvn clean install
```

Para compilar versiones no publicadas desde el código fuente, clona el repo y ejecuta `mvn clean install` localmente. Los releases se publican en Maven Central — ver [PUBLISHING.es.md](PUBLISHING.es.md) (mantenedores).

## Roadmap

- Workflow de release en GitHub Actions al pushear tags (ver [PUBLISHING.es.md](PUBLISHING.es.md))
- Soporte `TYPE_USE` en constraints (`List<@EmailAddress String>`)
- Mejoras de multipart en `ValidationExceptionHandler`
- `autoPublish=true` en Central Portal cuando la automatización de releases esté estable

## Licencia

Copyright © 2026 **Benjamín Olvera R.**

Licensed under the [Apache License, Version 2.0](LICENSE).
