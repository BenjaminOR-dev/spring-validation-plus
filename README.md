# Spring Validation Plus

[🇪🇸 Versión en español](README.es.md) | [🇧🇷 Versão em português](README.pt.md)

[![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%7C%204.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.x-blue)](https://jakarta.ee/specifications/bean-validation/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter?label=Maven%20Central)](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**Laravel-style** validations implemented as **Jakarta Validation** (Bean Validation) constraints, with automatic integration for **Spring Boot**.

Spring Validation Plus adds more than **85 annotations** (`@Required`, `@Same`, `@RequiredIf`, …) that work just like `@NotNull` or `@Size`: you put them on your DTOs and they run with `@Valid` or `@Validated`. Includes i18n messages (es/en/pt), unified JSON error handling, and optional database rules (`@Unique`, `@Exists`).

> **Jakarta Validation** is not something you install separately: it is the **standard** for validation in Java (`@Valid`, `@Constraint`). This library **extends** it with Laravel-style rules; the engine (Hibernate Validator) **already comes included** in the starter.

**Includes:**

- Laravel-style constraints (`@Required`, `@EmailAddress`, `@Same`, `@RequiredIf`, …)
- Cross-field rules on the **field** or on the **class** (`@RequiredWith("password")`, `@Same("email")`, …)
- Type validation (`@StringType`, `@IntegerType`, `@ArrayType`, …)
- JSON responses `{ "errors": { "field": ["message"] } }`
- Automatic translation of conversion errors (query params, mistyped JSON)
- Optional JPA integration for `@Unique` and `@Exists`
- Core usable without Spring Boot (`spring-validation-plus-core`)

<a id="why-use-validation-plus"></a>
## Why use Validation Plus?

Spring Boot already includes **Jakarta Validation**, but the standard only defines **~22 generic constraints** (`@NotNull`, `@Size`, `@Email`, `@Past`, …). It **does not include** cross-field rules, password confirmation, database uniqueness, or most format validations that Laravel provides out of the box.

**Validation Plus adds 85+ constraints** on top of the same engine (`@Valid`, Hibernate Validator): it does not replace Jakarta, it **completes** it with a Laravel-style DX.

**What Jakarta does not include but Validation Plus does:**

- **Cross-field** — `@RequiredWith`, `@Same`, `@RequiredIf`, `@Different`, `@ProhibitedIf`, …
- **Database** — `@Unique` and `@Exists`; requires JPA in **your** app ([how to install it](#jpa-for-unique-and-exists))
- **Types and presence** — `@Required`, `@Nullable`, `@StringType`, `@IntegerType`, `@ArrayType`, …
- **Formats** — `@EmailAddress`, `@Url`, `@Uuid`, `@Ip`, `@Json`, `@Password`, `@Regex`, `@In`, …

| With Jakarta / Spring only (~22 rules) | With Validation Plus (85+ rules) |
|----------------------------------------|----------------------------------|
| `@NotNull` does not reject `""` or `"   "` as "empty" | `@Required` treats null, empty, and whitespace-only like Laravel |
| No native field matching or `required_with` | `@Same("password")`, `@RequiredWith("password")` on the field |
| No `@Unique` / `@Exists`; uniqueness goes in the service | e.g. `@Unique(entity = User.class, field = "email")` on the DTO |
| Conditional rules → custom validators | `@RequiredIf`, `@RequiredUnless`, `@ProhibitedIf`, … |
| Generic English messages; i18n must be built | Messages in **es / en / pt** with `{field}` and `{other}` already resolved |
| Errors scattered across exceptions and conversions | Unified JSON `{ "errors": { "field": ["…"] } }` |
| Wrong types in query/JSON → technical messages | Friendly errors (`"The size field must be an integer"`) |

**When standard Jakarta is enough:** simple CRUD, few rules, no cross-field or DB checks, single language.

**When Validation Plus pays off:** matching fields (`@Same`), partial updates (`@Nullable`), DB uniqueness (`@Unique`), conditional rules, frontend-ready error responses, i18n from day one, or coming from Laravel/PHP.

It is still Jakarta Validation under the hood: you can mix `@NotNull` with `@Required` or `@Email` with `@EmailAddress` on the same DTO.

<a id="table-of-contents"></a>
## Table of contents

- [Why use Validation Plus?](#why-use-validation-plus)
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
  - [Cross-field rules](#cross-field-rules)
    - [On the field (recommended)](#on-the-field-recommended)
    - [On the class (alternative)](#on-the-class-alternative)
  - [Database (`@Unique`, `@Exists`)](#database-unique-exists)
- [Error response](#error-response)
- [Internationalization (i18n)](#internationalization-i18n)
- [Exception handler](#exception-handler)
- [Module architecture](#module-architecture)
- [Executable reference (example)](#executable-reference-example)
- [Troubleshooting](#troubleshooting)
- [Constraint reference](#constraint-reference)
- [Development](#development)
- [Roadmap](#roadmap)
- [License](#license)

<a id="requirements"></a>
## Requirements

- **Java 17+**
- **Spring Boot 3.x or 4.x** (same starter JAR)

| Spring Boot | Hibernate Validator (typical) | Validation Plus |
|-------------|-------------------------------|-----------------|
| 3.x | 8.x | Supported (default CI) |
| 4.x | 9.x | Supported (`mvn test -Phv9`) |

The same starter JAR works on both. Use **≥ 0.3.3**. Auto-config ordering (`beforeName` / `afterName`) and `{field}` interpolation are written for Boot 3 and Boot 4.

<a id="which-dependencies-do-i-install"></a>
### Which dependencies do I install?

In a Spring Boot app you **only need to add Validation Plus** for complete validation (constraints + engine + auto-config):

| Dependency | Do you add it? | When |
|-------------|----------------|--------|
| `spring-validation-plus-spring-boot-starter` | **Yes** | Always (constraints, i18n, JSON handler) |
| `spring-boot-starter-web` | Depends on your app | REST API (`@RestController`, `@RequestBody`) — **not** included in Validation Plus |
| `spring-boot-starter-data-jpa` | Only if needed | `@Unique` and `@Exists` — **not included** with Spring Boot or Validation Plus |

**Do not install separately** (the starter already brings them via Maven):

| Dependency | Reason |
|-------------|--------|
| `spring-boot-starter-validation` | Redundant: Validation Plus already includes it transitively |
| `jakarta.validation-api` | Included in the starter |
| Hibernate Validator | Included in the starter |

The **Jakarta Validation 3.x** badge indicates **compatibility** with the standard, not an extra installation step.

<a id="jpa-for-unique-and-exists"></a>
### JPA for `@Unique` and `@Exists`

**JPA is not included** in Spring Boot or Validation Plus. If you only use format, type, or cross-field rules (`@Required`, `@Same`, …), you do not need to install anything else.

For `@Unique` and `@Exists` to query the database you need to **add JPA yourself** in your project:

1. **`spring-boot-starter-data-jpa`** — Spring Boot auto-configures Hibernate when it detects it on the classpath. Validation Plus **does not** bring it as a transitive dependency.
2. **JDBC driver** for your database (H2 for development/testing; PostgreSQL, MySQL, etc. in production).
3. **DataSource** configured in `application.yml` / `application.properties`.

Example with **H2** (in-memory, no extra install — same approach as the `spring-validation-plus-example` module). In production, replace the driver with your DB driver:

**Maven:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Gradle (Kotlin DSL):**

```kotlin
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
runtimeOnly("com.h2database:h2")
```

With JPA active (an `EntityManagerFactory` at runtime), Validation Plus automatically registers the `@Unique` and `@Exists` checkers. If you do not use JPA, you can implement `UniquenessChecker` / `ExistenceChecker` manually — see [Database](#database-unique-exists).

<a id="without-spring-boot"></a>
### Without Spring Boot

Use the `spring-validation-plus-core` artifact and include a validation engine yourself (e.g. `hibernate-validator`). Details in [Module architecture](#module-architecture).

<a id="quick-start"></a>
## Quick start

<a id="1-dependency"></a>
### 1. Dependency

Add **only** the Validation Plus starter. Maven will resolve the rest (Jakarta Validation + Hibernate Validator):

**Maven**

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>0.3.3</version>
</dependency>
```

**Gradle (Kotlin DSL)**

```kotlin
implementation("io.github.benjaminor-dev:spring-validation-plus-spring-boot-starter:0.3.3")
```

**Gradle (Groovy)**

```groovy
implementation 'io.github.benjaminor-dev:spring-validation-plus-spring-boot-starter:0.3.3'
```

**Multi-module Maven** (same repository):

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>${project.version}</version>
</dependency>
```

> Available on [Maven Central](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter) — no extra repository configuration needed.

> **You do not need `spring-boot-starter-validation`** — Validation Plus already includes Jakarta Validation + Hibernate Validator. **Add `spring-boot-starter-web` only if your API is REST with Spring MVC** (`@RestController`, `@RequestBody`): Validation Plus does not install it; most web projects already have it.

<a id="2-annotate-your-dto"></a>
### 2. Annotate your DTO

```java
import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.StringType;

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

> All Java blocks in this README include **full imports** so you can copy and paste without guessing the origin.

<a id="3-validate-in-the-controller"></a>
### 3. Validate in the controller

```java
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@PostMapping("/users")
public ResponseEntity<User> create(@Valid @RequestBody UserCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
}
```

The starter automatically configures the `Validator` with i18n and, if enabled, a `ControllerAdvice` that returns errors in unified JSON format.

<a id="configuration"></a>
## Configuration

Available properties in `application.properties`:

```properties
# Enable/disable integration (default: true)
spring.validation-plus.enabled=true

# Enable the included ValidationExceptionHandler (default: true)
spring.validation-plus.exception-handler.enabled=true

# Default language when there is no Accept-Language header
spring.web.locale=es
spring.web.locale-resolver=accept_header
```

| Property | Default | Description |
|-----------|---------|-------------|
| `spring.validation-plus.enabled` | `true` | Validator, Spring integration, and JPA checkers (`@Unique` / `@Exists`) |
| `spring.validation-plus.exception-handler.enabled` | `true` | `ValidationExceptionHandler` for 400 errors in JSON |

If your app already has its own `@RestControllerAdvice` for validation, disable the library handler and delegate only business errors (404, 401, etc.) to your local advice.

<a id="usage-guide"></a>
## Usage guide

<a id="reference-imports"></a>
### Reference imports

| Source | Typical import | When |
|--------|---------------|--------|
| Validation Plus | `import dev.benjaminor.validationplus.constraints.*;` | `@Required`, `@EmailAddress`, `@Same`, … |
| Jakarta Validation | `import jakarta.validation.Valid;` | `@Valid` on body, nested lists |
| Spring Web | `import org.springframework.web.bind.annotation.*;` | REST controllers |
| Spring (path params) | `import org.springframework.validation.annotation.Validated;` | `@MinValue` on `@PathVariable` |
| Conditional operators | `import dev.benjaminor.validationplus.constraints.ConditionalOperator;` | `operator` in `@RequiredIf`, `@ProhibitedIf`, … |

All Java examples below include the necessary imports.

<a id="recommended-field-pattern"></a>
### Recommended field pattern

Stack annotations in this order:

1. **Presence** — `@Required` or `@Nullable`
2. **Type** — `@StringType`, `@IntegerType`, `@ArrayType`, etc.
3. **Business rules** — `@MinLength`, `@MinValue`, `@EmailAddress`, etc.

```java
import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MaxValue;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.MinValue;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.StringType;

public class ExampleRequest {

    @Required          // 1. required
    @StringType        // 2. must be String
    @MinLength(2)      // 3. at least 2 characters
    @MaxLength(50)     // 3. max 50 characters
    private String name;

    @IntegerType       // 2. must be Java integer
    @MinValue(0)        // 3. >= 0
    @MaxValue(100)     // 3. <= 100
    private Integer page;
}
```

| Java type | Type constraints | Range / format constraints |
|-----------|---------------------|--------------------------------|
| `String` | `@StringType` | `@MinLength`, `@MaxLength`, `@EmailAddress`, `@Regex`, `@In`, … |
| `Integer`, `Long`, … | `@IntegerType` | `@MinValue`, `@MaxValue`, `@Between`, `@Gt`, `@Lt`, … |
| `Double`, `Float`, `BigDecimal` | `@DecimalType` | `@MinValue`, `@MaxValue`, `@Digits`, … |
| `Boolean` | `@BooleanType` | `@Accepted`, `@Declined` |
| `List`, `Set`, array | `@ArrayType` | `@Between` (size), `@Size`, `@Distinct` |
| Dates (`LocalDate`, `String`) | `@Date`, `@DateFormat` | `@Before`, `@After`, `@BeforeOrEqual`, … |

> `@EmailAddress`, `@MinLength`, and similar **ignore null or blank values**. Combine them with `@Required` when the field is mandatory.

<a id="optional-fields-nullable"></a>
### Optional fields (`@Nullable`)

For fields that are **not required** in partial updates (password, last name, etc.):

```java
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.Nullable;
import dev.benjaminor.validationplus.constraints.StringType;

public class UserUpdateRequest {

    @Nullable
    @StringType
    @MinLength(6)
    @MaxLength(255)
    private String password;
}
```

| JSON value | `@Nullable` | `@MinLength(6)` | Result |
|---------------|-------------|-----------------|-----------|
| Field **omitted** | ✅ | — (null) | ✅ Pass |
| `"password": null` | ✅ | — (null) | ✅ Pass |
| `"password": ""` | ✅ | ❌ length 0 | **400** — must be at least 6 characters |
| `"password": "abc"` | ✅ | ❌ | **400** |
| `"password": "secret123"` | ✅ | ✅ | ✅ Pass |

**Practical rule:** if the client **does not want to change** the field, they must **omit it** or send `null`. An empty string `""` counts as a present value and length/format rules still apply.

The same applies to optional filters in query params: `@Nullable` + `@EmailAddress` allows leaving `email` empty in the URL.

<a id="json-body-requestbody"></a>
### JSON body (`@RequestBody`)

Standard flow for POST/PUT:

```
JSON → Jackson deserializes → @Valid runs constraints → controller
```

```java
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody UserCreateRequest request) { ... }
```

If the JSON has a wrong type (`"size": "abc"`), the `ValidationExceptionHandler` translates the Jackson error to a friendly i18n message.

<a id="query-params-and-forms-modelattribute"></a>
### Query params and forms (`@ModelAttribute`)

For GET with filters, pagination, or **POST** with `application/x-www-form-urlencoded`:

```java
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@GetMapping
public ResponseEntity<List<User>> search(@Valid @ModelAttribute UserSearchRequest request) { ... }

@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
public ResponseEntity<?> createForm(@Valid @ModelAttribute UserCreateRequest request) { ... }
```

```java
import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MaxValue;
import dev.benjaminor.validationplus.constraints.MinValue;
import dev.benjaminor.validationplus.constraints.Nullable;
import dev.benjaminor.validationplus.constraints.StringType;

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
|-----------|-----------|----------------|
| `?size=abc` (non-numeric) | Type conversion | `ValidationExceptionHandler` → `"The size field must be an integer."` |
| `?size=0` (invalid numeric) | `@MinValue(1)` | Validation constraint |
| `?email=foo` (bad email) | `@EmailAddress` | Validation constraint |

`@IntegerType` on an `Integer` field **does not intercept** non-numeric text in query params; that happens in the binding phase. Once converted correctly, `@IntegerType` and `@MinValue` do apply.

<a id="path-variables-validated"></a>
### Path variables (`@Validated`)

To validate route or method parameters (not DTOs), annotate the controller with `@Validated` and use constraints on the parameter:

```java
import dev.benjaminor.validationplus.constraints.MinValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public User find(@PathVariable @MinValue(1) Long id) { ... }
}
```

Errors are returned as `{ "errors": { "id": ["..."] } }` with i18n messages (same format as in the body).

<a id="arrays-and-lists"></a>
### Arrays and lists

There are **two levels** of validation:

| Level | What you validate | Constraints |
|-------|-------------|-------------|
| The array itself | Type, element count, no duplicates | `@ArrayType`, `@Between`, `@Size`, `@Distinct`, `@Required` |
| Each element | Email, length, own fields | **Child DTO + `@Valid`** (see next section) |

**List of simple values** (tags, IDs):

```java
import dev.benjaminor.validationplus.constraints.ArrayType;
import dev.benjaminor.validationplus.constraints.Between;
import dev.benjaminor.validationplus.constraints.Distinct;
import dev.benjaminor.validationplus.constraints.Required;

import java.util.List;

public class BulkTagRequest {

    @Required
    @ArrayType
    @Between(min = 1, max = 20)   // between 1 and 20 elements
    @Distinct                     // no duplicates
    private List<String> tags;
}
```

`@Between` works on numbers, string length, **and collection/array size**.

<a id="nested-validation-with-dtos"></a>
### Nested validation with DTOs

When each array item is an object with its own rules, use a child DTO and Jakarta's `@Valid`:

```java
import dev.benjaminor.validationplus.constraints.ArrayType;
import dev.benjaminor.validationplus.constraints.Between;
import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MinValue;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.StringType;
import jakarta.validation.Valid;

import java.util.List;

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

Nested errors in the response:

```json
{
  "errors": {
    "items[0].quantity": ["The quantity field must be at least 1."],
    "items": ["The items field must have between 1 and 50 elements."]
  }
}
```

> validation-plus constraints **do not yet support** inline annotations in generics (`List<@EmailAddress String>`). To validate each string in a list, use a wrapper DTO or a child DTO with `@Valid`.

<a id="cross-field-rules"></a>
### Cross-field rules

Constraints that relate several fields of the same DTO. You can place them **on the field being validated** (recommended, Laravel-style) or **on the class** (classic form).

<a id="on-the-field-recommended"></a>
#### On the field (recommended)

Place the annotation directly on the field that must satisfy the rule. The parameter indicates the observed field(s):

```java
import dev.benjaminor.validationplus.constraints.Same;
import dev.benjaminor.validationplus.constraints.RequiredIf;
import dev.benjaminor.validationplus.constraints.RequiredWith;
import dev.benjaminor.validationplus.constraints.Same;

public class PasswordChangeRequest {

    private String newPassword;
    private String password;
    private String role;

    @RequiredWith("newPassword")
    private String oldPassword;

    @RequiredIf(field = "role", value = "ADMIN")
    private String adminCode;

    @Same("password")
    private String passwordConfirmation;

    @Same("password")
    private String passwordConfirmationAlt;
}
```

| Constraint | Field syntax |
|---|---|
| `@RequiredWith` / `@RequiredWithout` | `@RequiredWith("observedField")` |
| `@RequiredWithAll` / `@RequiredWithoutAll` | `@RequiredWithAll({"a", "b"})` |
| `@RequiredIf` / `@RequiredUnless` | `@RequiredIf(field = "role", value = "ADMIN")` |
| Conditional operator (`operator`) | `@RequiredIf(field = "role", value = "GUEST", operator = ConditionalOperator.NOT_EQUALS)` |
| Multiple values (`IN`) | `@RequiredIf(field = "role", value = "ADMIN,MODERATOR", operator = ConditionalOperator.IN)` |
| `@RequiredIfAccepted` / `@RequiredIfDeclined` | `@RequiredIfAccepted("termsAccepted")` |
| `@Same` / `@Different` | `@Same("password")` |
| `@ProhibitedIf` / `@ProhibitedUnless` | `@ProhibitedIf(field = "role", value = "ADMIN")` |
| `@MissingIf` / `@MissingUnless` | `@MissingIf(field = "role", value = "GUEST")` |
| `@MissingWith` / `@MissingWithAll` | `@MissingWith("email")` |

> In constraints that observe multiple fields, `value` is an alias for `fields`: `@RequiredWith({"email", "phone"})`.

Conditional constraints (`@RequiredIf`, `@RequiredUnless`, `@ProhibitedIf`, `@ProhibitedUnless`, `@MissingIf`, `@MissingUnless`) compare `field` with `value` using `operator` (default `ConditionalOperator.EQUALS`):

| Operator | Meaning |
|----------|-------------|
| `EQUALS` (default) | The observed field equals `value` |
| `NOT_EQUALS` | The observed field differs from `value` |
| `IN` | The observed field matches one of the comma-separated values in `value` |

`@RequiredIfAccepted` / `@RequiredIfDeclined` do not use `operator` (they evaluate boolean acceptance of the observed field).

<a id="on-the-class-alternative"></a>
#### On the class (alternative)

The classic syntax is still supported when you prefer to centralize rules:

```java
import dev.benjaminor.validationplus.constraints.Same;
import dev.benjaminor.validationplus.constraints.RequiredIf;

@RequiredIf(field = "role", value = "ADMIN", required = "adminCode")
@Same(field = "password", other = "passwordConfirmation")
public class UserRequest {

    private String role;
    private String adminCode;
    private String password;
    private String passwordConfirmation;
}
```

See [Constraint reference → Cross-field](#cross-field).

<a id="database-unique-exists"></a>
### Database (`@Unique`, `@Exists`)

Validations that query persistence at runtime. They require a **checker** registered via SPI.

> **JPA is not included by default:** neither Spring Boot nor Validation Plus install JPA automatically. Add `spring-boot-starter-data-jpa` (plus driver and DataSource) following [JPA for `@Unique` and `@Exists`](#jpa-for-unique-and-exists), or register custom checkers.

<a id="jpa-integration-checklist"></a>
#### JPA integration checklist

1. `spring-boot-starter-data-jpa` dependency in your project (in addition to the validation-plus starter).
2. JPA entity with `@Entity` (e.g. `User.class`).
3. `@Unique` or `@Exists` constraint at **class level** on the DTO, pointing to the DTO field and JPA attribute.
4. On **updates**, use `excludeParameter` or `excludeField` to avoid comparing against the same record.

The starter automatically registers `JpaUniquenessChecker` and `JpaExistenceChecker` when it detects an `EntityManagerFactory` (after Hibernate auto-config). Database queries run in read-only transactions, also with `spring.jpa.open-in-view=false`.

<a id="custom-checkers-spring-boot"></a>
#### Custom checkers (Spring Boot)

Declare a bean instead of the default JPA ones — the starter registers it automatically:

```java
import dev.benjaminor.validationplus.spi.UniquenessChecker;
import org.springframework.context.annotation.Bean;

@Bean
UniquenessChecker uniquenessChecker(UserRepository repository) {
    return request -> repository.countByEmail((String) request.value()) == 0;
}
```

If a `UniquenessChecker` or `ExistenceChecker` bean exists, the JPA implementation is not created.

<a id="unique-parameters"></a>
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

<a id="create-unique-value"></a>
#### Create — unique value

```java
import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.StringType;
import dev.benjaminor.validationplus.constraints.Unique;

@Unique(entity = User.class, field = "email", column = "email")
public class UserCreateRequest {

    @Required
    @StringType
    @EmailAddress
    @MaxLength(255)
    private String email;
}
```

<a id="update-exclude-the-current-record"></a>
#### Update — exclude the current record

`excludeParameter` reads the id from the current HTTP request (path variable) via `RequestContextValueProvider`:

```java
import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.Unique;

@Unique(
    entity = User.class,
    field = "email",
    column = "email",
    excludeParameter = "id",
    message = "The email is already registered by another user."
)
public class UserUpdateRequest {

    @Required
    @EmailAddress
    private String email;
}
```

```java
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@PutMapping("/{id}")
public User update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) { ... }
//                                    ↑ must match excludeParameter = "id"
```

<a id="exists-the-record-must-exist"></a>
#### `@Exists` — the record must exist

```java
import dev.benjaminor.validationplus.constraints.Exists;
import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.constraints.Required;

@Exists(entity = Role.class, field = "roleId", column = "id")
public class AssignRoleRequest {

    @Required
    @IntegerType
    private Long roleId;
}
```

With **multiple** `EntityManagerFactory` beans (multi-datasource), set the persistence unit
(same name as `LocalContainerEntityManagerFactoryBean#setPersistenceUnitName` or the bean name).
Empty = `@Primary` / only EMF:

```java
@Exists(entity = Registro.class, field = "idRegistro", column = "idRegistro",
        persistenceUnit = "nomina")
@Exists(entity = Estatus.class, field = "idEstatus", column = "idEstatus",
        persistenceUnit = "nomina")
public class PersistenciaEstatusRequest { ... }
```

`@Unique` accepts the same `persistenceUnit` attribute.

<a id="non-jpa-backend-manual-spi"></a>
#### Non-JPA backend (manual SPI)

Outside Spring Boot, or if you prefer explicit registration, use SPI registration at startup:

```java
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;

ValidationPlusCheckers.registerUniquenessChecker(request -> {
    // return true if the value is unique
});
ValidationPlusCheckers.registerExistenceChecker(request -> {
    // return true if the record exists
});
```

For HTTP context values (path variables), register a `ContextValueProvider`. In Spring Boot web apps, `RequestContextValueProvider` is included by default; you can replace it with your own `@Bean ContextValueProvider`.

<a id="common-unique-errors"></a>
#### Common `@Unique` errors

| Message | Cause | Solution |
|---------|-------|----------|
| `No uniqueness checker configured for {field}` | No checker registered | Add `spring-boot-starter-data-jpa` or register a manual `UniquenessChecker` |
| Email exists but is the **same** record on update | Current id not excluded | Use `excludeParameter = "id"` aligned with `@PathVariable` |
| JPA error / entity not found | Wrong `entity` or `column` | `column` = **attribute** name on the Java entity |

<a id="error-response"></a>
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

The handler covers:

- `MethodArgumentNotValidException` / `BindException` — JSON body, query params, and form fields
- `MethodArgumentTypeMismatchException` — path or query params with incompatible types
- `HttpMessageNotReadableException` — malformed JSON or wrong types in the body
- `HandlerMethodValidationException` / `ConstraintViolationException` — `@Validated` on controller methods (path variables, method parameters)

Conversion errors (`typeMismatch`) are translated to i18n with `FieldErrorMessageResolver` and `TypeMismatchMessageUtils` (`dev.benjaminor.validationplus.type.integer`, etc.).

<a id="internationalization-i18n"></a>
## Internationalization (i18n)

Messages included in the core:

| File | Language |
|---------|--------|
| `ValidationMessages.properties` | English (default) |
| `ValidationMessages_es.properties` | Spanish |
| `ValidationMessages_pt.properties` | Portuguese |

**Language selection:**

1. Header `Accept-Language: es`, `Accept-Language: pt`, or `Accept-Language: en`
2. Fallback: `spring.web.locale=es` (Spring Boot)

**Override messages** in your app — create `src/main/resources/ValidationMessages_es.properties` (or the locale you use). Only define the keys you want to change; the rest fall back to the library bundle.

```properties
dev.benjaminor.validationplus.constraints.Required.message=The {field} field is required.
```

For a single field, use `message` on the annotation:

```java
import dev.benjaminor.validationplus.constraints.Required;

public class ExampleRequest {

    @Required(message = "Name is required")
    private String name;
}
```

**Message templates** (copy keys from the included files):

- [ValidationMessages.properties](spring-validation-plus-core/src/main/resources/ValidationMessages.properties) — English
- [ValidationMessages_es.properties](spring-validation-plus-core/src/main/resources/ValidationMessages_es.properties) — Spanish
- [ValidationMessages_pt.properties](spring-validation-plus-core/src/main/resources/ValidationMessages_pt.properties) — Portuguese

**Available placeholders:** `{field}`, `{other}`, `{value}`, `{values}`, `{min}`, `{max}`, `{condition}`, `{format}`, `{integer}`, `{fraction}`, `{validatedValue}`

| Placeholder | Typical use |
|---|---|
| `{field}` | Field where the error is reported |
| `{other}` | Observed field(s) in cross-field rules (e.g. the companion in `@RequiredWith`) |
| `{value}` | Trigger value in conditional rules (`@RequiredIf`, `@ProhibitedIf`, …); CSV lists render as `A, B` |
| `{values}` | Lists from `@In` / MIME types / extensions |
| `{min}` / `{max}` | Numeric or size limits |
| `{condition}` | Localized operator phrase (`is` / `is not` / `is one of`, …) |
| `{format}` | `@DateFormat` pattern |
| `{integer}` / `{fraction}` | `@Digits` counts |

The `{field}` and `{other}` placeholders are resolved by the included interpolator (`ValidationPlusMessageInterpolator`). If you see them unreplaced (or an empty field name), use starter **≥ 0.3.3** and verify you are not defining a custom `LocalValidatorFactoryBean` without that interpolator.

<a id="exception-handler"></a>
## Exception handler

By default, `ValidationExceptionHandler` is registered as `@RestControllerAdvice`. To use only the validator and your own advice:

```properties
spring.validation-plus.exception-handler.enabled=false
```

Your advice must handle at least `MethodArgumentNotValidException` and `BindException` if you want custom 400 responses. Query param conversion errors (`typeMismatch` in `FieldError`) are translated internally by `FieldErrorMessageResolver` when the library handler is active.

<a id="module-architecture"></a>
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
└── spring-validation-plus-example/              # Not publishable. Executable reference.
    └── README.md                                # DTO → pattern → curl map
```

| Maven artifact | When to use it |
|-----------------|---------------|
| `spring-validation-plus-spring-boot-starter` | Spring Boot apps (recommended) |
| `spring-validation-plus-core` | Jakarta Validation projects without Spring, or custom frameworks |

**Auto-configuration included in the starter:**

| Class | Responsibility |
|-------|-----------------|
| `SpringValidationPlusAutoConfiguration` | `LocalValidatorFactoryBean` with i18n, `ValidationExceptionHandler`, `RequestContextValueProvider` |
| `JpaValidationPlusAutoConfiguration` | `JpaUniquenessChecker` + `JpaExistenceChecker` (if JPA is present) |

<a id="executable-reference-example"></a>
## Executable reference (example)

The **`spring-validation-plus-example`** module is **living** documentation: runnable code + guide with curls.

```bash
docker compose up example   # http://localhost:8080
```

See **[spring-validation-plus-example/README.es.md](spring-validation-plus-example/README.es.md)** — includes:

- **DTO → pattern → endpoint** map
- Examples of `@Unique`, `@ModelAttribute`, nested `@Valid`, `@RequiredIf`, `@Same`
- In-memory H2 to test database rules without installing anything extra

<a id="troubleshooting"></a>
## Troubleshooting

<a id="messages-appear-in-english"></a>
### Messages appear in English

Postman and many clients **do not send** `Accept-Language`. Configure default language:

```properties
spring.web.locale=es
spring.web.locale-resolver=accept_header
```

Or send the `Accept-Language: es` header on each request.

<a id="invalid-numeric-query-param-shows-error-in-english"></a>
### Invalid numeric query param shows error in English

Enable the library handler:

```properties
spring.validation-plus.exception-handler.enabled=true
```

If you use your own `@RestControllerAdvice`, you must translate `typeMismatch` errors in `FieldError` or reuse the logic from `FieldErrorMessageResolver`.

<a id="unique-responds-no-uniqueness-checker-configured"></a>
### `@Unique` responds "No uniqueness checker configured"

1. Confirm `spring-boot-starter-data-jpa` is on the classpath.
2. Confirm the app starts with JPA (DataSource + entities).
3. Use a starter version that includes `JpaValidationPlusAutoConfiguration` (activates **after** Hibernate).
4. Without JPA: implement and register `UniquenessChecker` manually (see [Manual SPI without JPA](#non-jpa-backend-manual-spi)).

<a id="minlength-fails-on-a-field-i-want-to-leave-empty"></a>
### `@MinLength` fails on a field I want to leave empty

`""` is **not** the same as `null`. Omit the field from JSON or send `"field": null`. See [Optional fields](#optional-fields-nullable).

<a id="nested-validation-does-not-run-on-a-list"></a>
### Nested validation does not run on a list

Missing Jakarta `@Valid` on the collection:

```java
import dev.benjaminor.validationplus.constraints.ArrayType;
import dev.benjaminor.validationplus.constraints.Between;
import dev.benjaminor.validationplus.constraints.Required;
import jakarta.validation.Valid;

import java.util.List;

public class CreateOrderRequest {

    @Required
    @ArrayType
    @Between(min = 1, max = 50)
    @Valid
    private List<OrderItemRequest> items;
}
```

<a id="i-want-to-disable-only-the-handler-not-the-validator"></a>
### I want to disable only the handler, not the validator

```properties
spring.validation-plus.exception-handler.enabled=false
spring.validation-plus.enabled=true
```

<a id="constraint-reference"></a>
## Constraint reference

<a id="field"></a>
### Field

| Constraint | Description |
|---|---|
| `@Required` | Required field (not null, not empty) |
| `@Nullable` | Documents that it can be null; never fails |
| `@Filled` | Not empty if present |
| `@StringType` | Must be `String` / `CharSequence` |
| `@IntegerType` | Must be Java integer (`Integer`, `Long`, `Byte`, …) |
| `@DecimalType` | `Float`, `Double`, or `BigDecimal` |
| `@BooleanType` | Real `Boolean` |
| `@ArrayType` | Array or `Collection` |
| `@Numeric` | Any `Number` |
| `@EmailAddress` | Valid email (ignores blank) |
| `@MinLength` / `@MaxLength` | Minimum/maximum length |
| `@MinValue` / `@MaxValue` | Minimum/maximum numeric value |
| `@Between` | Numeric range, text length, or collection size |
| `@Size` | Exact size (text, collection, or number) |
| `@Accepted` / `@Declined` | Truthy/falsy values: `true`/`false`, `yes`/`no`, `on`/`off`, `1`/`0`, strings `true`/`false`, `T`/`F` (case-insensitive) |
| `@In` / `@NotIn` | Value within/outside a list |
| `@MustBe` | Value must equal exactly one constant (e.g. `@MustBe("T")`) |
| `@Regex` / `@NotRegex` | Matches/does not match pattern |
| `@Url` | Valid URL |
| `@ActiveUrl` | http(s) URL with host (no DNS/HTTP) |
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
| `@EnumValue` | Java enum value |
| `@Password` | Configurable password policy (`min`, `letters`, `numbers`, …) |
| `@Distinct` | Unique values in array/collection |
| `@File` | Uploaded file (`MultipartFile`, `Part`) with optional size/type |
| `@Image` | Uploaded image with optional dimensions and size |

<a id="cross-field"></a>
### Cross-field

Support **field** level (recommended) and **class** level. See [Cross-field rules](#cross-field-rules).

| Constraint | Description |
|---|---|
| `@RequiredIf` | Required if another field meets the condition (`operator`: `EQUALS`, `NOT_EQUALS`, `IN`) |
| `@RequiredUnless` | Required unless another field meets the condition |
| `@RequiredWith` / `@RequiredWithout` | Required if any companion is present/absent |
| `@RequiredWithAll` / `@RequiredWithoutAll` | Required if all/total absence of companions |
| `@RequiredIfAccepted` / `@RequiredIfDeclined` | Required if field accepted/declined |
| `@Same` / `@Different` | Two fields must match / differ |
| `@Prohibited` / `@ProhibitedIf` / `@ProhibitedUnless` | Field prohibited (conditional) |
| `@Missing` / `@MissingIf` / `@MissingUnless` | Field must be absent (conditional) |
| `@MissingWith` / `@MissingWithAll` | Absent if companion(s) present |
| `@InArray` | Value must exist in another array/collection field |

<a id="database-class-level-spi"></a>
### Database (class level, SPI)

| Constraint | Description |
|---|---|
| `@Unique` | Unique value in table/entity |
| `@Exists` | Record must exist in table/entity |

> `@Unique` and `@Exists` are `@Repeatable`: you can declare multiple rules on the same DTO.

<a id="development"></a>
## Development

```text
spring-validation-plus/
├── spring-validation-plus-core/
├── spring-validation-plus-spring-boot-starter/
└── spring-validation-plus-example/    ← executable reference (see README.es.md)
```

```bash
# Build and run all tests
docker compose run --rm maven

# Core tests only
docker compose run --rm maven mvn -pl spring-validation-plus-core test

# Reference app (port 8080)
docker compose up example

# Install to local .m2 (e.g. to test a SNAPSHOT from main)
docker compose run --rm maven mvn clean install
```

To build unpublished versions from source, clone the repo and run `mvn clean install` locally. Releases are published to Maven Central — see [PUBLISHING.md](PUBLISHING.md) (maintainers).

<a id="roadmap"></a>
## Roadmap

- `TYPE_USE` support in constraints (`List<@EmailAddress String>`)
- Multipart improvements in `ValidationExceptionHandler`

<a id="license"></a>
## License

Copyright © 2026 **Benjamín Olvera R.**

Licensed under the [Apache License, Version 2.0](LICENSE).
