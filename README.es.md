# Spring Validation Plus

[🇬🇧 English version](README.md) | [🇧🇷 Versão em português](README.pt.md)

[![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%7C%204.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.x-blue)](https://jakarta.ee/specifications/bean-validation/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter?label=Maven%20Central)](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Validaciones estilo **Laravel** implementadas como **constraints de Jakarta Validation** (Bean Validation), con integración automática para **Spring Boot**.

Spring Validation Plus añade más de **85 anotaciones** (`@Required`, `@Confirmed`, `@RequiredIf`, …) que funcionan igual que `@NotNull` o `@Size`: las pones en tus DTOs y se ejecutan con `@Valid` o `@Validated`. Incluye mensajes i18n (es/en/pt), manejo unificado de errores JSON y soporte opcional para reglas de base de datos (`@Unique`, `@Exists`).

> **Jakarta Validation** no es algo que instales aparte: es el **estándar** de validación en Java (`@Valid`, `@Constraint`). Esta librería lo **extiende** con reglas estilo Laravel; el motor (Hibernate Validator) **ya viene incluido** en el starter.

**Incluye:**

- Constraints estilo Laravel (`@Required`, `@EmailAddress`, `@Confirmed`, `@RequiredIf`, …)
- Reglas entre campos en el **campo** o en la **clase** (`@RequiredWith("password")`, `@Same("email")`, …)
- Validación de tipos (`@StringType`, `@IntegerType`, `@ArrayType`, …)
- Respuestas JSON `{ "errors": { "campo": ["mensaje"] } }`
- Traducción automática de errores de conversión (query params, JSON mal tipado)
- Integración JPA opcional para `@Unique` y `@Exists`
- Core usable sin Spring Boot (`spring-validation-plus-core`)

## ¿Por qué usar Validation Plus?

Spring Boot ya trae **Jakarta Validation**, pero el estándar solo define **~22 constraints** genéricos (`@NotNull`, `@Size`, `@Email`, `@Past`, …). **No incluye** reglas entre campos, confirmación de contraseña, unicidad en BD ni la mayoría de validaciones de formato que Laravel trae listas.

**Validation Plus suma 85+ constraints** sobre el mismo motor (`@Valid`, Hibernate Validator): no sustituye Jakarta, lo **completa** con una DX estilo Laravel.

**Lo que Jakarta no trae y aquí sí:**

- **Entre campos** — `@RequiredWith`, `@Confirmed`, `@RequiredIf`, `@Same`, `@Different`, `@ProhibitedIf`, …
- **Base de datos** — `@Unique` y `@Exists`; requiere JPA en **tu** app ([cómo instalarlo](#jpa-para-unique-y-exists))
- **Tipos y presencia** — `@Required`, `@Nullable`, `@StringType`, `@IntegerType`, `@ArrayType`, …
- **Formatos** — `@EmailAddress`, `@Url`, `@Uuid`, `@Ip`, `@Json`, `@Password`, `@Regex`, `@In`, …

| Con solo Jakarta / Spring (~22 reglas) | Con Validation Plus (85+ reglas) |
|----------------------------------------|----------------------------------|
| `@NotNull` no rechaza `""` ni `"   "` como “vacío” | `@Required` trata null, vacío y solo espacios como Laravel |
| Sin `@Confirmed` ni `required_with` nativos | `@Confirmed("password")`, `@RequiredWith("password")` en el campo |
| Sin `@Unique` / `@Exists`; la unicidad va en el servicio | p. ej. `@Unique(entity = User.class, field = "email")` en el DTO |
| Reglas condicionales → validadores custom | `@RequiredIf`, `@RequiredUnless`, `@ProhibitedIf`, … |
| Mensajes genéricos en inglés; i18n hay que montarla | Mensajes en **es / en / pt** con `{field}` y `{other}` ya resueltos |
| Errores repartidos entre excepciones y conversiones | JSON unificado `{ "errors": { "campo": ["…"] } }` |
| Tipos incorrectos en query/JSON → mensajes técnicos | Errores amigables (`"El campo size debe ser un entero"`) |

**Cuándo te basta Jakarta estándar:** CRUD simples, pocas reglas, sin cross-field ni checks en BD, un solo idioma.

**Cuándo compensa Validation Plus:** confirmación de contraseña, updates parciales (`@Nullable`), unicidad en BD (`@Unique`), reglas condicionales, respuestas de error listas para el frontend, i18n desde el día uno, o vienes de Laravel/PHP.

Sigue siendo Jakarta Validation por debajo: puedes mezclar `@NotNull` con `@Required` o `@Email` con `@EmailAddress` en el mismo DTO.

## Tabla de contenidos

- [¿Por qué usar Validation Plus?](#por-qué-usar-validation-plus)
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
  - [Reglas entre campos (cross-field)](#reglas-entre-campos-cross-field)
    - [En el campo (recomendado)](#en-el-campo-recomendado)
    - [En la clase (alternativa)](#en-la-clase-alternativa)
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

- **Java 17+**
- **Spring Boot 3.x o 4.x** (mismo JAR del starter)

| Spring Boot | Hibernate Validator (típico) | Validation Plus |
|-------------|------------------------------|-----------------|
| 3.x | 8.x | Soportado (CI por defecto) |
| 4.x | 9.x | Soportado (`mvn test -Phv9`) |

El mismo JAR del starter sirve en ambos. Usa **≥ 0.3.3**. El orden de auto-config (`beforeName` / `afterName`) y la interpolación de `{field}` están pensados para Boot 3 y Boot 4.

### ¿Qué dependencias instalo yo?

En una app Spring Boot **solo necesitas añadir Validation Plus** para tener validación completa (constraints + motor + auto-config):

| Dependencia | ¿La añades tú? | Cuándo |
|-------------|----------------|--------|
| `spring-validation-plus-spring-boot-starter` | **Sí** | Siempre (constraints, i18n, handler JSON) |
| `spring-boot-starter-web` | Según tu app | REST API (`@RestController`, `@RequestBody`) — **no** viene en Validation Plus |
| `spring-boot-starter-data-jpa` | Solo si aplica | `@Unique` y `@Exists` — **no viene** con Spring Boot ni con Validation Plus |

**No instales por separado** (el starter ya los trae vía Maven):

| Dependencia | Motivo |
|-------------|--------|
| `spring-boot-starter-validation` | Redundante: Validation Plus ya lo incluye transitivamente |
| `jakarta.validation-api` | Incluida en el starter |
| Hibernate Validator | Incluido en el starter |

El badge **Jakarta Validation 3.x** indica **compatibilidad** con el estándar, no un paso extra de instalación.

### JPA para `@Unique` y `@Exists`

**JPA no viene incluido** en Spring Boot ni en Validation Plus. Si solo usas reglas de formato, tipos o entre campos (`@Required`, `@Confirmed`, …), no necesitas instalar nada más.

Para que `@Unique` y `@Exists` consulten la base de datos necesitas **añadir JPA tú mismo** en tu proyecto:

1. **`spring-boot-starter-data-jpa`** — Spring Boot auto-configura Hibernate cuando lo detecta en el classpath. Validation Plus **no** lo trae como dependencia transitiva.
2. **Driver JDBC** de tu base de datos (H2 para desarrollo/pruebas; PostgreSQL, MySQL, etc. en producción).
3. **DataSource** configurado en `application.yml` / `application.properties`.

Ejemplo con **H2** (memoria, sin instalar nada extra — el mismo enfoque que el módulo `spring-validation-plus-example`). En producción, sustituye el driver por el de tu BD:

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

Con JPA activo (un `EntityManagerFactory` en runtime), Validation Plus registra automáticamente los checkers de `@Unique` y `@Exists`. Si no usas JPA, puedes implementar `UniquenessChecker` / `ExistenceChecker` manualmente — ver [Base de datos](#base-de-datos-unique-exists).

### Sin Spring Boot

Usa el artefacto `spring-validation-plus-core` e incluye tú un motor de validación (p. ej. `hibernate-validator`). Detalle en [Arquitectura de módulos](#arquitectura-de-módulos).

## Inicio rápido

### 1. Dependencia

Añade **solo** el starter de Validation Plus. Maven resolverá el resto (Jakarta Validation + Hibernate Validator):

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

**Multi-módulo Maven** (mismo repositorio):

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>${project.version}</version>
</dependency>
```

> Disponible en [Maven Central](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter) — no hace falta configurar repositorios extra.

> **`spring-boot-starter-validation` no hace falta** — Validation Plus ya incluye Jakarta Validation + Hibernate Validator. **`spring-boot-starter-web` solo si tu API es REST con Spring MVC** (`@RestController`, `@RequestBody`): Validation Plus no lo instala; en la práctica casi todos los proyectos web ya lo tienen.

### 2. Anota tu DTO

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

> Todos los bloques Java de este README incluyen **imports completos** para que puedas copiar y pegar sin dudas de origen.

### 3. Valida en el controller

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

El starter configura automáticamente el `Validator` con i18n y, si está habilitado, un `ControllerAdvice` que devuelve errores en formato JSON unificado.

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

### Imports de referencia

| Origen | Import típico | Cuándo |
|--------|---------------|--------|
| Validation Plus | `import dev.benjaminor.validationplus.constraints.*;` | `@Required`, `@EmailAddress`, `@Confirmed`, … |
| Jakarta Validation | `import jakarta.validation.Valid;` | `@Valid` en body, listas anidadas |
| Spring Web | `import org.springframework.web.bind.annotation.*;` | Controllers REST |
| Spring (path params) | `import org.springframework.validation.annotation.Validated;` | `@MinValue` en `@PathVariable` |
| Operadores condicionales | `import dev.benjaminor.validationplus.constraints.ConditionalOperator;` | `operator` en `@RequiredIf`, `@ProhibitedIf`, … |

Todos los ejemplos Java más abajo incluyen los imports necesarios.

### Patrón recomendado por campo

Apila las anotaciones en este orden:

1. **Presencia** — `@Required` o `@Nullable`
2. **Tipo** — `@StringType`, `@IntegerType`, `@ArrayType`, etc.
3. **Reglas de negocio** — `@MinLength`, `@MinValue`, `@EmailAddress`, etc.

```java
import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MaxValue;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.MinValue;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.StringType;

public class ExampleRequest {

    @Required          // 1. obligatorio
    @StringType        // 2. debe ser String
    @MinLength(2)      // 3. al menos 2 caracteres
    @MaxLength(50)     // 3. máximo 50 caracteres
    private String name;

    @IntegerType       // 2. debe ser entero Java
    @MinValue(0)        // 3. >= 0
    @MaxValue(100)     // 3. <= 100
    private Integer page;
}
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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody UserCreateRequest request) { ... }
```

Si el JSON trae un tipo incorrecto (`"size": "abc"`), el `ValidationExceptionHandler` traduce el error de Jackson a un mensaje i18n amigable.

### Query params y formularios (`@ModelAttribute`)

Para GET con filtros, paginación o **POST** con `application/x-www-form-urlencoded`:

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

Los errores se devuelven como `{ "errors": { "id": ["..."] } }` con mensajes i18n (mismo formato que en el body).

### Arrays y listas

Hay **dos niveles** de validación:

| Nivel | Qué validas | Constraints |
|-------|-------------|-------------|
| El array en sí | Tipo, cantidad de elementos, sin duplicados | `@ArrayType`, `@Between`, `@Size`, `@Distinct`, `@Required` |
| Cada elemento | Email, longitud, campos propios | **DTO hijo + `@Valid`** (ver siguiente sección) |

**Lista de valores simples** (tags, IDs):

```java
import dev.benjaminor.validationplus.constraints.ArrayType;
import dev.benjaminor.validationplus.constraints.Between;
import dev.benjaminor.validationplus.constraints.Distinct;
import dev.benjaminor.validationplus.constraints.Required;

import java.util.List;

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
    @Valid                                    // ← valida cada OrderItemRequest
    private List<OrderItemRequest> items;
}
```

Errores anidados en la respuesta:

```json
{
  "errors": {
    "items[0].quantity": ["El campo quantity debe ser al menos 1."],
    "items": ["El campo items debe tener entre 1 y 50 elementos."]
  }
}
```

> Los constraints de validation-plus **no soportan aún** anotaciones inline en generics (`List<@EmailAddress String>`). Para validar cada string de una lista, usa un DTO wrapper o un DTO hijo con `@Valid`.

### Reglas entre campos (cross-field)

Constraints que relacionan varios campos del mismo DTO. Puedes colocarlos **en el campo que se valida** (recomendado, estilo Laravel) o **en la clase** (forma clásica).

#### En el campo (recomendado)

Coloca la anotación directamente sobre el campo que debe cumplir la regla. El parámetro indica el/los campo(s) observado(s):

```java
import dev.benjaminor.validationplus.constraints.Confirmed;
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

    @Confirmed("password")
    private String passwordConfirmationAlt;
}
```

| Constraint | Sintaxis en el campo |
|---|---|
| `@RequiredWith` / `@RequiredWithout` | `@RequiredWith("campoObservado")` |
| `@RequiredWithAll` / `@RequiredWithoutAll` | `@RequiredWithAll({"a", "b"})` |
| `@RequiredIf` / `@RequiredUnless` | `@RequiredIf(field = "role", value = "ADMIN")` |
| Operador condicional (`operator`) | `@RequiredIf(field = "role", value = "GUEST", operator = ConditionalOperator.NOT_EQUALS)` |
| Varios valores (`IN`) | `@RequiredIf(field = "role", value = "ADMIN,MODERATOR", operator = ConditionalOperator.IN)` |
| `@RequiredIfAccepted` / `@RequiredIfDeclined` | `@RequiredIfAccepted("termsAccepted")` |
| `@Same` / `@Different` | `@Same("password")` |
| `@Confirmed` | `@Confirmed("password")` o `@Confirmed` si el campo termina en `Confirmation` |
| `@ProhibitedIf` / `@ProhibitedUnless` | `@ProhibitedIf(field = "role", value = "ADMIN")` |
| `@MissingIf` / `@MissingUnless` | `@MissingIf(field = "role", value = "GUEST")` |
| `@MissingWith` / `@MissingWithAll` | `@MissingWith("email")` |

> En constraints que observan varios campos, `value` es alias de `fields`: `@RequiredWith({"email", "phone"})`.

Los constraints condicionales (`@RequiredIf`, `@RequiredUnless`, `@ProhibitedIf`, `@ProhibitedUnless`, `@MissingIf`, `@MissingUnless`) comparan `field` con `value` usando `operator` (por defecto `ConditionalOperator.EQUALS`):

| Operador | Significado |
|----------|-------------|
| `EQUALS` (default) | El campo observado es igual a `value` |
| `NOT_EQUALS` | El campo observado es distinto de `value` |
| `IN` | El campo observado coincide con alguno de los valores separados por coma en `value` |

`@RequiredIfAccepted` / `@RequiredIfDeclined` no usan `operator` (evalúan aceptación booleana del campo observado).

#### En la clase (alternativa)

Sigue soportada la sintaxis clásica cuando prefieres centralizar las reglas:

```java
import dev.benjaminor.validationplus.constraints.Confirmed;
import dev.benjaminor.validationplus.constraints.RequiredIf;

@RequiredIf(field = "role", value = "ADMIN", required = "adminCode")
@Confirmed(field = "password")
public class UserRequest {

    private String role;
    private String adminCode;
    private String password;
    private String passwordConfirmation;
}
```

Ver [Referencia de constraints → Entre campos](#entre-campos).

### Base de datos (`@Unique`, `@Exists`)

Validaciones que consultan persistencia en runtime. Requieren un **checker** registrado vía SPI.

> **JPA no viene por defecto:** ni Spring Boot ni Validation Plus instalan JPA automáticamente. Añade `spring-boot-starter-data-jpa` (más driver y DataSource) siguiendo [JPA para `@Unique` y `@Exists`](#jpa-para-unique-y-exists), o registra checkers custom.

#### Checklist de integración JPA

1. Dependencia `spring-boot-starter-data-jpa` en tu proyecto (además del starter de validation-plus).
2. Entidad JPA con `@Entity` (p. ej. `User.class`).
3. Constraint `@Unique` o `@Exists` a **nivel clase** del DTO, apuntando al campo del DTO y al atributo JPA.
4. En **updates**, usar `excludeParameter` o `excludeField` para no comparar contra el propio registro.

El starter registra automáticamente `JpaUniquenessChecker` y `JpaExistenceChecker` cuando detecta un `EntityManagerFactory` (después de la auto-config de Hibernate). Las consultas de base de datos se ejecutan en transacción de solo lectura, también con `spring.jpa.open-in-view=false`.

#### Checkers custom (Spring Boot)

Declara un bean en lugar de los defaults JPA — el starter lo registra automáticamente:

```java
import dev.benjaminor.validationplus.spi.UniquenessChecker;
import org.springframework.context.annotation.Bean;

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

#### Actualizar — excluir el registro actual

`excludeParameter` lee el id desde la petición HTTP actual (path variable) mediante `RequestContextValueProvider`:

```java
import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.Unique;

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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@PutMapping("/{id}")
public User update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) { ... }
//                                    ↑ debe coincidir con excludeParameter = "id"
```

#### `@Exists` — el registro debe existir

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

Con **varios** `EntityManagerFactory` (multi-datasource), indica el persistence unit
(el mismo nombre que en `LocalContainerEntityManagerFactoryBean#setPersistenceUnitName`
o el bean name). Vacío = EMF `@Primary` / único:

```java
@Exists(entity = Registro.class, field = "idRegistro", column = "idRegistro",
        persistenceUnit = "nomina")
@Exists(entity = Estatus.class, field = "idEstatus", column = "idEstatus",
        persistenceUnit = "nomina")
public class PersistenciaEstatusRequest { ... }
```

`@Unique` acepta el mismo atributo `persistenceUnit`.

#### Backend sin JPA (SPI manual)

Fuera de Spring Boot, o si prefieres registro explícito, usa el registro SPI al arrancar:

```java
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;

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
| `ValidationMessages_es.properties` | Español |
| `ValidationMessages_pt.properties` | Portugués |

**Selección de idioma:**

1. Header `Accept-Language: es`, `Accept-Language: pt` o `Accept-Language: en`
2. Fallback: `spring.web.locale=es` (Spring Boot)

**Sobrescribir mensajes** en tu app — crea `src/main/resources/ValidationMessages_es.properties` (o el locale que uses). Solo define las claves que quieras cambiar; el resto hace fallback al bundle de la librería.

```properties
dev.benjaminor.validationplus.constraints.Required.message=El campo {field} es requerido.
```

Para un solo campo, usa `message` en la anotación:

```java
import dev.benjaminor.validationplus.constraints.Required;

public class ExampleRequest {

    @Required(message = "El nombre es obligatorio")
    private String name;
}
```

**Plantillas de mensajes** (copia claves desde los archivos incluidos):

- [ValidationMessages.properties](spring-validation-plus-core/src/main/resources/ValidationMessages.properties) — inglés
- [ValidationMessages_es.properties](spring-validation-plus-core/src/main/resources/ValidationMessages_es.properties) — español
- [ValidationMessages_pt.properties](spring-validation-plus-core/src/main/resources/ValidationMessages_pt.properties) — portugués

**Placeholders disponibles:** `{field}`, `{other}`, `{value}`, `{values}`, `{min}`, `{max}`, `{condition}`, `{format}`, `{integer}`, `{fraction}`, `{validatedValue}`

| Placeholder | Uso típico |
|---|---|
| `{field}` | Campo donde se reporta el error |
| `{other}` | Campo(s) observado(s) en reglas entre campos (p. ej. el companion en `@RequiredWith`) |
| `{value}` | Valor disparador en reglas condicionales (`@RequiredIf`, `@ProhibitedIf`, …); listas CSV se muestran como `A, B` |
| `{values}` | Listas de `@In` / MIME / extensiones |
| `{min}` / `{max}` | Límites numéricos o de tamaño |
| `{condition}` | Frase localizada del operador (`es` / `no es` / `es uno de`, …) |
| `{format}` | Patrón de `@DateFormat` |
| `{integer}` / `{fraction}` | Dígitos de `@Digits` |

Los placeholders `{field}` y `{other}` los resuelve el interpolador incluido (`ValidationPlusMessageInterpolator`). Si los ves sin reemplazar (o el nombre del campo vacío), usa el starter **≥ 0.3.3** y verifica que no hayas definido un `LocalValidatorFactoryBean` custom sin ese interpolador.

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

### Entre campos (cross-field)

Soportan nivel **campo** (recomendado) y nivel **clase**. Ver [Reglas entre campos](#reglas-entre-campos-cross-field).

| Constraint | Descripción |
|---|---|
| `@RequiredIf` | Obligatorio si otro campo cumple la condición (`operator`: `EQUALS`, `NOT_EQUALS`, `IN`) |
| `@RequiredUnless` | Obligatorio salvo que otro campo cumpla la condición |
| `@RequiredWith` / `@RequiredWithout` | Obligatorio si algún companion está presente/ausente |
| `@RequiredWithAll` / `@RequiredWithoutAll` | Obligatorio si todos/ausencia total de companions |
| `@RequiredIfAccepted` / `@RequiredIfDeclined` | Obligatorio si campo aceptado/rechazado |
| `@Same` / `@Different` | Dos campos deben coincidir / ser distintos |
| `@Confirmed` | Debe coincidir con `{field}Confirmation` (o campo indicado) |
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

Para compilar versiones no publicadas desde el código fuente, clona el repo y ejecuta `mvn clean install` localmente. Los releases se publican en Maven Central — ver [PUBLISHING.md](PUBLISHING.md) (mantenedores).

## Roadmap

- Soporte `TYPE_USE` en constraints (`List<@EmailAddress String>`)
- Mejoras de multipart en `ValidationExceptionHandler`
- `autoPublish=true` en Central Portal cuando la automatización de releases esté estable

## Licencia

Copyright © 2026 **Benjamín Olvera R.**

Licensed under the [Apache License, Version 2.0](LICENSE).
