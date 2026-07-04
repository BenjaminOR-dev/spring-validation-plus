# Spring Validation Plus

[🇪🇸 Versión en español](README.es.md)

[![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%7C%204.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.x-blue)](https://jakarta.ee/specifications/bean-validation/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter?label=Maven%20Central)](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**Laravel-inspired** validation for **Spring Boot** and **Jakarta Validation**.

Spring Validation Plus adds more than **85 custom constraints** that work like native Jakarta Validation annotations: you annotate your DTOs and they run with `@Valid` or `@Validated`. It includes i18n messages (en/es), unified JSON error handling, and optional database rules (`@Unique`, `@Exists`).

**Includes:**

- Laravel-style constraints (`@Required`, `@EmailAddress`, `@Confirmed`, `@RequiredIf`, …)
- Type validation (`@StringType`, `@IntegerType`, `@ArrayType`, …)
- JSON responses `{ "errors": { "field": ["message"] } }`
- Automatic translation of conversion errors (query params, mistyped JSON)
- Optional JPA integration for `@Unique` and `@Exists`
- Core usable without Spring Boot (`spring-validation-plus-core`)

## Table of contents

- [Requirements](#requirements)
- [Quick start](#quick-start)
- [Configuration](#configuration)
- [Usage guide](#usage-guide)
  - [Recommended field pattern](#recommended-field-pattern)
  - [Optional fields (`@Nullable`)](#optional-fields-nullable)
  - [JSON body (`@RequestBody`)](#json-body-requestbody)
  - [Query params and forms (`@ModelAttribute`)](#query-params-and-forms-modelattribute)
  - [Path variables (`@Validated`)](#path-variables-validated)
  - [Arrays and lists](#arrays-and-lists)
  - [Nested validation with DTOs](#nested-validation-with-dtos)
  - [Class-level rules](#class-level-rules)
  - [Database (`@Unique`, `@Exists`)](#database-unique-exists)
- [Error response](#error-response)
- [Internationalization (i18n)](#internationalization-i18n)
- [Exception handler](#exception-handler)
- [Module architecture](#module-architecture)
- [Runnable reference (example)](#runnable-reference-example)
- [Troubleshooting](#troubleshooting)
- [Constraint reference](#constraint-reference)
- [Development](#development)
- [Roadmap](#roadmap)
- [License](#license)

## Requirements

- Java 17+
- Spring Boot 3.x / 4.x
- Jakarta Validation 3.x

## Quick start

### 1. Dependency

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

**Multi-module Maven** (same repository):

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>${project.version}</version>
</dependency>
```

> Available on [Maven Central](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter) — no extra repositories required.

**Optional dependencies**

| You need | Also add |
|----------|----------|
| Validation only (no Spring Boot) | `spring-validation-plus-core` |
| `@Unique` / `@Exists` rules with JPA | `spring-boot-starter-data-jpa` |

### 2. Annotate your DTO

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

### 3. Validate in the controller

```java
@PostMapping("/users")
public ResponseEntity<User> create(@Valid @RequestBody UserCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
}
```

The starter automatically configures the `Validator` with i18n and, when enabled, a `ControllerAdvice` that returns errors in a unified JSON format.

> **Note:** your project must include Jakarta validation (`spring-boot-starter-validation` or `spring-boot-starter-web`, which brings it transitively).

## Configuration

Available properties in `application.properties`:

```properties
# Enable/disable integration (default: true)
spring.validation-plus.enabled=true

# Enable the bundled ValidationExceptionHandler (default: true)
spring.validation-plus.exception-handler.enabled=true

# Default locale when no Accept-Language header is present
spring.web.locale=en
spring.web.locale-resolver=accept_header
```

| Property | Default | Description |
|----------|---------|-------------|
| `spring.validation-plus.enabled` | `true` | Validator, Spring integration, and JPA checkers (`@Unique` / `@Exists`) |
| `spring.validation-plus.exception-handler.enabled` | `true` | `ValidationExceptionHandler` for 400 errors in JSON |

If your app already has its own `@RestControllerAdvice` for validation, disable the library handler and delegate only business errors (404, 401, etc.) to your local advice.

## Usage guide

### Recommended field pattern

Stack annotations in this order:

1. **Presence** — `@Required` or `@Nullable`
2. **Type** — `@StringType`, `@IntegerType`, `@ArrayType`, etc.
3. **Business rules** — `@MinLength`, `@MinValue`, `@EmailAddress`, etc.

```java
@Required          // 1. required
@StringType        // 2. must be String
@MinLength(2)      // 3. at least 2 characters
@MaxLength(50)     // 3. at most 50 characters
private String name;

@IntegerType       // 2. must be a Java integer
@MinValue(0)        // 3. >= 0
@MaxValue(100)     // 3. <= 100
private Integer page;
```

| Java type | Type constraints | Range / format constraints |
|-----------|------------------|----------------------------|
| `String` | `@StringType` | `@MinLength`, `@MaxLength`, `@EmailAddress`, `@Regex`, `@In`, … |
| `Integer`, `Long`, … | `@IntegerType` | `@MinValue`, `@MaxValue`, `@Between`, `@Gt`, `@Lt`, … |
| `Double`, `Float`, `BigDecimal` | `@DecimalType` | `@MinValue`, `@MaxValue`, `@Digits`, … |
| `Boolean` | `@BooleanType` | `@Accepted`, `@Declined` |
| `List`, `Set`, array | `@ArrayType` | `@Between` (size), `@Size`, `@Distinct` |
| Dates (`LocalDate`, `String`) | `@Date`, `@DateFormat` | `@Before`, `@After`, `@BeforeOrEqual`, … |

> `@EmailAddress`, `@MinLength`, and similar constraints **ignore null or blank values**. Combine them with `@Required` when the field is mandatory.

### Optional fields (`@Nullable`)

For fields that are **not required** in partial updates (password, last name, etc.):

```java
@Nullable
@StringType
@MinLength(6)
@MaxLength(255)
private String password;
```

| JSON value | `@Nullable` | `@MinLength(6)` | Result |
|------------|-------------|-----------------|--------|
| Field **omitted** | ✅ | — (null) | ✅ Passes |
| `"password": null` | ✅ | — (null) | ✅ Passes |
| `"password": ""` | ✅ | ❌ length 0 | **400** — must be at least 6 characters |
| `"password": "abc"` | ✅ | ❌ | **400** |
| `"password": "secret123"` | ✅ | ✅ | ✅ Passes |

**Practical rule:** if the client **does not want to change** the field, they must **omit it** or send `null`. An empty string `""` counts as a present value and length/format rules still apply.

The same applies to optional filters in query params: `@Nullable` + `@EmailAddress` allows leaving `email` empty in the URL.

### JSON body (`@RequestBody`)

Standard flow for POST/PUT:

```
JSON → Jackson deserializes → @Valid runs constraints → controller
```

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody UserCreateRequest request) { ... }
```

If the JSON has an incorrect type (`"size": "abc"`), the `ValidationExceptionHandler` translates the Jackson error into a friendly i18n message.

### Query params and forms (`@ModelAttribute`)

For GET with filters, pagination, or **POST** with `application/x-www-form-urlencoded`:

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

**Important:** Spring converts query params **before** running `@Valid`.

| Scenario | What fails | Who responds |
|----------|------------|--------------|
| `?size=abc` (non-numeric) | Type conversion | `ValidationExceptionHandler` → `"The size field must be an integer."` |
| `?size=0` (invalid numeric) | `@MinValue(1)` | Validation constraint |
| `?email=foo` (bad email) | `@EmailAddress` | Validation constraint |

`@IntegerType` on an `Integer` field **does not intercept** non-numeric text in query params; that happens during the binding phase. Once converted correctly, `@IntegerType` and `@MinValue` do apply.

> Message text follows the request locale (`Accept-Language`) or `spring.web.locale`. With `spring.web.locale=es`, the same error would be: `"El campo size debe ser un entero."`

### Path variables (`@Validated`)

To validate route or method parameters (not DTOs), annotate the controller with `@Validated` and use constraints on the parameter:

```java
@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public User find(@PathVariable @MinValue(1) Long id) { ... }
}
```

Validation errors are returned as `{ "errors": { "id": ["..."] } }` with i18n messages (same format as body validation).

### Arrays and lists

There are **two levels** of validation:

| Level | What you validate | Constraints |
|-------|-------------------|-------------|
| The array itself | Type, element count, no duplicates | `@ArrayType`, `@Between`, `@Size`, `@Distinct`, `@Required` |
| Each element | Email, length, own fields | **Child DTO + `@Valid`** (see next section) |

**List of simple values** (tags, IDs):

```java
public class BulkTagRequest {

    @Required
    @ArrayType
    @Between(min = 1, max = 20)   // between 1 and 20 elements
    @Distinct                     // no duplicates
    private List<String> tags;
}
```

`@Between` works on numbers, string length, **and collection/array size**.

### Nested validation with DTOs

When each array item is an object with its own rules, use a child DTO and Jakarta's `@Valid`:

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
    @Valid                                    // ← validates each OrderItemRequest
    private List<OrderItemRequest> items;
}
```

Nested errors in the response (English, `Accept-Language: en`):

```json
{
  "errors": {
    "items[0].quantity": ["The quantity field must be at least 1.0."],
    "items": ["The items field must have between 1.0 and 50.0 elements."]
  }
}
```

> validation-plus constraints **do not yet support** inline annotations on generics (`List<@EmailAddress String>`). To validate each string in a list, use a wrapper DTO or a child DTO with `@Valid`.

### Class-level rules

Constraints that relate several fields on the same DTO:

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

See [Constraint reference → Cross-field (class level)](#cross-field-class-level).

### Database (`@Unique`, `@Exists`)

Validations that query persistence at runtime. They require a **checker** registered via SPI.

#### JPA integration checklist

1. `spring-boot-starter-data-jpa` dependency in your project (in addition to the validation-plus starter).
2. JPA entity with `@Entity` (e.g. `User.class`).
3. `@Unique` or `@Exists` constraint at **class level** on the DTO, pointing to the DTO field and the JPA attribute.
4. On **updates**, use `excludeParameter` or `excludeField` so the current record is not compared against itself.

The starter automatically registers `JpaUniquenessChecker` and `JpaExistenceChecker` when it detects an `EntityManagerFactory` (after Hibernate auto-config). Database checks run in a read-only transaction, including when `spring.jpa.open-in-view=false`.

#### Custom checkers (Spring Boot)

Declare a bean instead of JPA defaults — the starter registers it automatically:

```java
@Bean
UniquenessChecker uniquenessChecker(UserRepository repository) {
    return request -> repository.countByEmail((String) request.value()) == 0;
}
```

If a `UniquenessChecker` or `ExistenceChecker` bean exists, the JPA implementation is not created.

#### `@Unique` parameters

| Parameter | Description |
|-----------|-------------|
| `entity` | `@Entity` class queried |
| `field` | **DTO** field validated and where the error is reported |
| `column` | **JPA attribute** used in JPQL (`e.email`, not necessarily the physical SQL column name) |
| `excludeField` | DTO field with the id to exclude (alternative to `excludeParameter`) |
| `excludeParameter` | Name of the `@PathVariable` or query param with the id (e.g. `"id"` in `PUT /users/{id}`) |
| `excludeColumn` | Entity identifier attribute (default: `"id"`) |
| `ignoreCase` | Case-insensitive comparison for strings (default: `true`) |
| `message` | Custom message (supports i18n with `{...}` keys) |

#### Create — unique value

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

#### Update — exclude the current record

`excludeParameter` reads the id from the current HTTP request (path variable) via `RequestContextValueProvider`:

```java
@Unique(
    entity = User.class,
    field = "email",
    column = "email",
    excludeParameter = "id",
    message = "This email is already registered by another user."
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
//                                    ↑ must match excludeParameter = "id"
```

#### `@Exists` — record must exist

```java
@Exists(entity = Role.class, field = "roleId", column = "id")
public class AssignRoleRequest {

    @Required
    @IntegerType
    private Long roleId;
}
```

#### Non-JPA backend (manual SPI)

Outside Spring Boot, or when you prefer explicit registration, use the SPI registry at startup:

```java
ValidationPlusCheckers.registerUniquenessChecker(request -> {
    // return true if the value is unique
});
ValidationPlusCheckers.registerExistenceChecker(request -> {
    // return true if the record exists
});
```

For HTTP context values (path variables), register a `ContextValueProvider`. In Spring Boot web apps, `RequestContextValueProvider` is included by default; you can replace it with your own `@Bean ContextValueProvider`.

#### Common `@Unique` errors

| Message | Cause | Fix |
|---------|-------|-----|
| `No uniqueness checker configured for {field}` | No checker registered | Add `spring-boot-starter-data-jpa` or register a `UniquenessChecker` manually |
| Email exists but it is the **same** record on update | Current id not excluded | Use `excludeParameter = "id"` aligned with `@PathVariable` |
| JPA error / entity not found | Wrong `entity` or `column` | `column` = **attribute** name on the Java entity |

## Error response

Laravel-style JSON format, returned by `ValidationExceptionHandler`:

```json
{
  "errors": {
    "email": ["The email field is required."],
    "size": ["The size field must be an integer."]
  }
}
```

> Messages are resolved via i18n. Send `Accept-Language: es` (or set `spring.web.locale=es`) for Spanish, e.g. `"El campo email es obligatorio."`

The handler covers:

- `MethodArgumentNotValidException` / `BindException` — JSON body, query params, and form fields
- `MethodArgumentTypeMismatchException` — path or query parameters with incompatible types
- `HttpMessageNotReadableException` — malformed JSON or wrong types in body
- `HandlerMethodValidationException` / `ConstraintViolationException` — `@Validated` on controller methods (path variables, method parameters)

Type conversion errors (`typeMismatch`) are translated to i18n via `FieldErrorMessageResolver` and `TypeMismatchMessageUtils` (`dev.benjaminor.validationplus.type.integer`, etc.).

## Internationalization (i18n)

Messages bundled in the core:

| File | Language |
|------|----------|
| `ValidationMessages.properties` | English (default) |
| `ValidationMessages_en.properties` | English |
| `ValidationMessages_es.properties` | Spanish |

**Language selection:**

1. Header `Accept-Language: es` or `Accept-Language: en`
2. Fallback: `spring.web.locale=en` (Spring Boot)

**Override messages** in your app — create `src/main/resources/ValidationMessages_en.properties`:

```properties
dev.benjaminor.validationplus.constraints.Required.message=The {field} field is required.
```

**Available placeholders:** `{field}`, `{min}`, `{max}`, `{value}`, `{other}`, `{validatedValue}`, `{integer}`, `{fraction}`

## Exception handler

By default, `ValidationExceptionHandler` is registered as `@RestControllerAdvice`. To use only the validator and your own advice:

```properties
spring.validation-plus.exception-handler.enabled=false
```

Your advice should handle at least `MethodArgumentNotValidException` and `BindException` if you want custom 400 responses. Query param conversion errors (`typeMismatch` in `FieldError`) are translated internally by `FieldErrorMessageResolver` when the library handler is active.

## Module architecture

```text
spring-validation-plus/
├── spring-validation-plus-core/                 # Publishable. No Spring Boot.
│   ├── constraints/                             # @Required, @EmailAddress, @Unique, …
│   ├── validators/                              # Jakarta Validation implementations
│   ├── support/                                 # i18n, utilities
│   ├── spi/                                     # UniquenessChecker, ExistenceChecker, …
│   └── resources/ValidationMessages*.properties
│
├── spring-validation-plus-spring-boot-starter/  # Publishable. Spring Boot auto-config.
│   ├── autoconfigure/                           # Validator, JPA checkers, locale
│   ├── exception/                               # ValidationExceptionHandler
│   └── jpa/                                     # JpaUniquenessChecker, JpaExistenceChecker
│
└── spring-validation-plus-example/              # Not publishable. Runnable reference.
    └── README.md                                # DTO → pattern → curl map
```

| Maven artifact | When to use |
|----------------|-------------|
| `spring-validation-plus-spring-boot-starter` | Spring Boot apps (recommended) |
| `spring-validation-plus-core` | Jakarta Validation without Spring, or custom frameworks |

**Auto-configuration included in the starter:**

| Class | Responsibility |
|-------|----------------|
| `SpringValidationPlusAutoConfiguration` | `LocalValidatorFactoryBean` with i18n, `ValidationExceptionHandler`, `RequestContextValueProvider` |
| `JpaValidationPlusAutoConfiguration` | `JpaUniquenessChecker` + `JpaExistenceChecker` (when JPA is present) |

## Runnable reference (example)

The **`spring-validation-plus-example`** module is **living documentation**: runnable code + a guide with curls.

```bash
docker compose up example   # http://localhost:8080
```

See **[spring-validation-plus-example/README.es.md](spring-validation-plus-example/README.es.md)** (Spanish) — it includes:

- **DTO → pattern → endpoint** map
- Examples of `@Unique`, `@ModelAttribute`, nested `@Valid`, `@RequiredIf`, `@Confirmed`
- In-memory H2 to try database rules without installing anything extra

An English version of the example guide is available at **[spring-validation-plus-example/README.md](spring-validation-plus-example/README.md)**.

## Troubleshooting

### Messages appear in English

Postman and many clients **do not send** `Accept-Language`. Configure a default locale:

```properties
spring.web.locale=es
spring.web.locale-resolver=accept_header
```

Or send the header `Accept-Language: es` on each request.

### Invalid numeric query param shows an error in English

Enable the library handler:

```properties
spring.validation-plus.exception-handler.enabled=true
```

If you use your own `@RestControllerAdvice`, you must translate `typeMismatch` errors in `FieldError` or reuse the logic from `FieldErrorMessageResolver`.

### `@Unique` responds "No uniqueness checker configured"

1. Confirm `spring-boot-starter-data-jpa` is on the classpath.
2. Confirm the app starts with JPA (DataSource + entities).
3. Use a starter version that includes `JpaValidationPlusAutoConfiguration` (activates **after** Hibernate).
4. Without JPA: implement and register `UniquenessChecker` manually (see [custom SPI](#non-jpa-backend-custom-spi)).

### `@MinLength` fails on a field I want to leave empty

`""` is **not** the same as `null`. Omit the field from the JSON or send `"field": null`. See [Optional fields](#optional-fields-nullable).

### Nested validation does not run on a list

Missing Jakarta `@Valid` on the collection:

```java
@Valid
private List<ItemRequest> items;
```

### I want to disable only the handler, not the validator

```properties
spring.validation-plus.exception-handler.enabled=false
spring.validation-plus.enabled=true
```

## Constraint reference

### Field

| Constraint | Description |
|---|---|
| `@Required` | Required field (not null, not empty) |
| `@Nullable` | Documents that null is allowed; never fails |
| `@Filled` | Not empty when present |
| `@StringType` | Must be `String` / `CharSequence` |
| `@IntegerType` | Must be a Java integer (`Integer`, `Long`, `Byte`, …) |
| `@DecimalType` | `Float`, `Double`, or `BigDecimal` |
| `@BooleanType` | Real `Boolean` |
| `@ArrayType` | Array or `Collection` |
| `@Numeric` | Any `Number` |
| `@EmailAddress` | Valid email (ignores blank) |
| `@MinLength` / `@MaxLength` | Minimum/maximum length |
| `@MinValue` / `@MaxValue` | Minimum/maximum numeric value |
| `@Between` | Numeric range, text length, or collection size |
| `@Size` | Exact size (text, collection, or number) |
| `@Accepted` / `@Declined` | Acceptance values (yes/no, true/false, 1/0) |
| `@In` / `@NotIn` | Value in/out of a list |
| `@Regex` / `@NotRegex` | Matches/does not match pattern |
| `@Url` | Valid URL |
| `@ActiveUrl` | Valid http(s) URL with host (no DNS/HTTP check) |
| `@Uuid` | Valid UUID |
| `@Ulid` | Valid ULID |
| `@HexColor` | Hexadecimal color |
| `@Date` | Valid date (temporal or string) |
| `@DateFormat` | Date with required format |
| `@Before` / `@After` | Date before/after another |
| `@BeforeOrEqual` / `@AfterOrEqual` | Date before/after inclusive |
| `@Digits` | Exact integer and decimal digits |
| `@MinDigits` / `@MaxDigits` / `@DigitsBetween` | Digit count |
| `@StartsWith` / `@EndsWith` | Text prefix/suffix |
| `@NotStartsWith` / `@NotEndsWith` | Does not start/end with text |
| `@Json` | Valid JSON string |
| `@Alpha` / `@AlphaNum` / `@AlphaDash` | Letters only, alphanumeric, alphanumeric with dashes |
| `@Ascii` | ASCII characters only |
| `@Lowercase` / `@Uppercase` | Lowercase / uppercase |
| `@Ip` / `@Ipv4` / `@Ipv6` / `@MacAddress` | Network addresses |
| `@Timezone` | Valid timezone |
| `@Gt` / `@Gte` / `@Lt` / `@Lte` | Strict numeric comparisons |
| `@MultipleOf` | Multiple of a number |
| `@EnumValue` | Value from a Java enum |
| `@Password` | Configurable password policy (`min`, `letters`, `numbers`, …) |
| `@Distinct` | Unique values in array/collection |
| `@File` | Uploaded file (`MultipartFile`, `Part`) with optional size/type |
| `@Image` | Uploaded image with optional dimensions and size |

### Cross-field (class level)

| Constraint | Description |
|---|---|
| `@RequiredIf` | Required when another field has a value |
| `@RequiredUnless` | Required unless another field has a value |
| `@RequiredWith` / `@RequiredWithout` | Required if any companion is present/absent |
| `@RequiredWithAll` / `@RequiredWithoutAll` | Required if all companions present / all absent |
| `@RequiredIfAccepted` / `@RequiredIfDeclined` | Required if field accepted/declined |
| `@Same` / `@Different` | Two fields must match / differ |
| `@Confirmed` | Must match `{field}Confirmation` |
| `@Prohibited` / `@ProhibitedIf` / `@ProhibitedUnless` | Field prohibited (conditional) |
| `@Missing` / `@MissingIf` / `@MissingUnless` | Field must be absent (conditional) |
| `@MissingWith` / `@MissingWithAll` | Absent when companion(s) present |
| `@InArray` | Value must exist in another array/collection field |

### Database (class level, SPI)

| Constraint | Description |
|---|---|
| `@Unique` | Unique value in table/entity |
| `@Exists` | Record must exist in table/entity |

> `@Unique` and `@Exists` are `@Repeatable`: you can declare several rules on the same DTO.

## Development

```text
spring-validation-plus/
├── spring-validation-plus-core/
├── spring-validation-plus-spring-boot-starter/
└── spring-validation-plus-example/    ← runnable reference (see its README.md)
```

```bash
# Build and run all tests
docker compose run --rm maven

# Core tests only
docker compose run --rm maven mvn -pl spring-validation-plus-core test

# Reference app (port 8080)
docker compose up example

# Install to local .m2 (e.g. to try a SNAPSHOT from main)
docker compose run --rm maven mvn clean install
```

To build unreleased versions from source, clone the repo and run `mvn clean install` locally. Releases are published to Maven Central — see [PUBLISHING.md](PUBLISHING.md) (maintainers).

## Roadmap

- `TYPE_USE` support in constraints (`List<@EmailAddress String>`)
- Multipart file handling improvements in `ValidationExceptionHandler`
- JPA end-to-end integration tests in CI

## License

Copyright © 2026 **Benjamín Olvera R.**

Licensed under the [Apache License, Version 2.0](LICENSE).
