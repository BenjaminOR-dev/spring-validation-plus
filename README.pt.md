# Spring Validation Plus

[🇬🇧 English version](README.md) | [🇪🇸 Versión en español](README.es.md)

[![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%7C%204.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.x-blue)](https://jakarta.ee/specifications/bean-validation/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter?label=Maven%20Central)](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Validações estilo **Laravel** implementadas como **constraints de Jakarta Validation** (Bean Validation), com integração automática para **Spring Boot**.

Spring Validation Plus adiciona mais de **85 anotações** (`@Required`, `@Confirmed`, `@RequiredIf`, …) que funcionam como `@NotNull` ou `@Size`: você as coloca nos seus DTOs e elas são executadas com `@Valid` ou `@Validated`. Inclui mensagens i18n (es/en/pt), tratamento unificado de erros JSON e suporte opcional para regras de banco de dados (`@Unique`, `@Exists`).

> **Jakarta Validation** não é algo que você instala separadamente: é o **padrão** de validação em Java (`@Valid`, `@Constraint`). Esta biblioteca o **estende** com regras estilo Laravel; o motor (Hibernate Validator) **já vem incluído** no starter.

**Inclui:**

- Constraints estilo Laravel (`@Required`, `@EmailAddress`, `@Confirmed`, `@RequiredIf`, …)
- Regras entre campos no **campo** ou na **classe** (`@RequiredWith("password")`, `@Same("email")`, …)
- Validação de tipos (`@StringType`, `@IntegerType`, `@ArrayType`, …)
- Respostas JSON `{ "errors": { "campo": ["mensagem"] } }`
- Tradução automática de erros de conversão (query params, JSON com tipo errado)
- Integração JPA opcional para `@Unique` e `@Exists`
- Core utilizável sem Spring Boot (`spring-validation-plus-core`)

## Por que usar o Validation Plus?

Spring Boot já traz **Jakarta Validation**, mas o padrão define apenas **~22 constraints** genéricos (`@NotNull`, `@Size`, `@Email`, `@Past`, …). **Não inclui** regras entre campos, confirmação de senha, unicidade em BD nem a maioria das validações de formato que o Laravel traz prontas.

**Validation Plus soma 85+ constraints** sobre o mesmo motor (`@Valid`, Hibernate Validator): não substitui o Jakarta, o **completa** com uma DX estilo Laravel.

**O que o Jakarta não traz e aqui sim:**

- **Entre campos** — `@RequiredWith`, `@Confirmed`, `@RequiredIf`, `@Same`, `@Different`, `@ProhibitedIf`, …
- **Banco de dados** — `@Unique` e `@Exists`; requer JPA na **sua** app ([como instalar](#jpa-para-unique-e-exists))
- **Tipos e presença** — `@Required`, `@Nullable`, `@StringType`, `@IntegerType`, `@ArrayType`, …
- **Formatos** — `@EmailAddress`, `@Url`, `@Uuid`, `@Ip`, `@Json`, `@Password`, `@Regex`, `@In`, …

| Com apenas Jakarta / Spring (~22 regras) | Com Validation Plus (85+ regras) |
|----------------------------------------|----------------------------------|
| `@NotNull` não rejeita `""` nem `"   "` como "vazio" | `@Required` trata null, vazio e só espaços como o Laravel |
| Sem `@Confirmed` nem `required_with` nativos | `@Confirmed("password")`, `@RequiredWith("password")` no campo |
| Sem `@Unique` / `@Exists`; a unicidade vai no serviço | ex.: `@Unique(entity = User.class, field = "email")` no DTO |
| Regras condicionais → validadores custom | `@RequiredIf`, `@RequiredUnless`, `@ProhibitedIf`, … |
| Mensagens genéricas em inglês; i18n precisa ser montada | Mensagens em **es / en / pt** com `{field}` e `{other}` já resolvidos |
| Erros espalhados entre exceções e conversões | JSON unificado `{ "errors": { "campo": ["…"] } }` |
| Tipos incorretos em query/JSON → mensagens técnicas | Erros amigáveis (`"O campo size deve ser um inteiro"`) |

**Quando o Jakarta padrão basta:** CRUDs simples, poucas regras, sem cross-field nem checks em BD, um único idioma.

**Quando o Validation Plus compensa:** confirmação de senha, updates parciais (`@Nullable`), unicidade em BD (`@Unique`), regras condicionais, respostas de erro prontas para o frontend, i18n desde o primeiro dia, ou vindo do Laravel/PHP.

Continua sendo Jakarta Validation por baixo: você pode misturar `@NotNull` com `@Required` ou `@Email` com `@EmailAddress` no mesmo DTO.

## Índice

- [Por que usar o Validation Plus?](#por-que-usar-o-validation-plus)
- [Requisitos](#requisitos)
- [Início rápido](#inicio-rapido)
- [Configuração](#configuracao)
- [Guia de uso](#guia-de-uso)
  - [Padrão recomendado por campo](#padrao-recomendado-por-campo)
  - [Campos opcionais (`@Nullable`)](#campos-opcionais-nullable)
  - [Body JSON (`@RequestBody`)](#body-json-requestbody)
  - [Query params e formulários (`@ModelAttribute`)](#query-params-e-formularios-modelattribute)
  - [Path variables (`@Validated`)](#path-variables-validated)
  - [Arrays e listas](#arrays-e-listas)
  - [Validação aninhada com DTOs](#validacao-aninhada-com-dtos)
  - [Regras entre campos (cross-field)](#regras-entre-campos-cross-field)
    - [No campo (recomendado)](#no-campo-recomendado)
    - [Na classe (alternativa)](#na-classe-alternativa)
  - [Banco de dados (`@Unique`, `@Exists`)](#banco-de-dados-unique-exists)
- [Resposta de erros](#resposta-de-erros)
- [Internacionalização (i18n)](#internacionalizacao-i18n)
- [Handler de exceções](#handler-de-excecoes)
- [Arquitetura de módulos](#arquitetura-de-modulos)
- [Referência executável (example)](#referencia-executavel-example)
- [Solução de problemas](#solucao-de-problemas)
- [Referência de constraints](#referencia-de-constraints)
- [Desenvolvimento](#desenvolvimento)
- [Roadmap](#roadmap)
- [Licença](#licenca)

## Requisitos

- **Java 17+**
- **Spring Boot 3.x ou 4.x** (mesmo JAR do starter)

| Spring Boot | Hibernate Validator (típico) | Validation Plus |
|-------------|------------------------------|-----------------|
| 3.x | 8.x | Suportado (CI padrão) |
| 4.x | 9.x | Suportado (`mvn test -Phv9`) |

O mesmo JAR do starter serve nos dois. Use **≥ 0.3.3**. A ordem de auto-config (`beforeName` / `afterName`) e a interpolação de `{field}` foram feitas para Boot 3 e Boot 4.

### Quais dependências eu instalo?

Em uma app Spring Boot **você só precisa adicionar o Validation Plus** para ter validação completa (constraints + motor + auto-config):

| Dependência | Você adiciona? | Quando |
|-------------|----------------|--------|
| `spring-validation-plus-spring-boot-starter` | **Sim** | Sempre (constraints, i18n, handler JSON) |
| `spring-boot-starter-web` | Conforme sua app | REST API (`@RestController`, `@RequestBody`) — **não** vem no Validation Plus |
| `spring-boot-starter-data-jpa` | Só se aplicável | `@Unique` e `@Exists` — **não vem** com Spring Boot nem com Validation Plus |

**Não instale separadamente** (o starter já os traz via Maven):

| Dependência | Motivo |
|-------------|--------|
| `spring-boot-starter-validation` | Redundante: Validation Plus já o inclui transitivamente |
| `jakarta.validation-api` | Incluída no starter |
| Hibernate Validator | Incluído no starter |

O badge **Jakarta Validation 3.x** indica **compatibilidade** com o padrão, não um passo extra de instalação.

### JPA para `@Unique` e `@Exists`

**JPA não vem incluído** no Spring Boot nem no Validation Plus. Se você só usa regras de formato, tipos ou entre campos (`@Required`, `@Confirmed`, …), não precisa instalar nada mais.

Para que `@Unique` e `@Exists` consultem o banco de dados você precisa **adicionar JPA você mesmo** no seu projeto:

1. **`spring-boot-starter-data-jpa`** — Spring Boot auto-configura o Hibernate quando o detecta no classpath. Validation Plus **não** o traz como dependência transitiva.
2. **Driver JDBC** do seu banco de dados (H2 para desenvolvimento/testes; PostgreSQL, MySQL, etc. em produção).
3. **DataSource** configurado em `application.yml` / `application.properties`.

Exemplo com **H2** (memória, sem instalar nada extra — mesma abordagem do módulo `spring-validation-plus-example`). Em produção, substitua o driver pelo do seu BD:

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

Com JPA ativo (um `EntityManagerFactory` em runtime), Validation Plus registra automaticamente os checkers de `@Unique` e `@Exists`. Se não usar JPA, você pode implementar `UniquenessChecker` / `ExistenceChecker` manualmente — ver [Banco de dados](#banco-de-dados-unique-exists).

### Sem Spring Boot

Use o artefato `spring-validation-plus-core` e inclua você mesmo um motor de validação (ex.: `hibernate-validator`). Detalhes em [Arquitetura de módulos](#arquitetura-de-modulos).

## Início rápido

### 1. Dependência

Adicione **apenas** o starter do Validation Plus. Maven resolverá o resto (Jakarta Validation + Hibernate Validator):

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

**Multi-módulo Maven** (mesmo repositório):

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>${project.version}</version>
</dependency>
```

> Disponível no [Maven Central](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter) — não é necessário configurar repositórios extras.

> **`spring-boot-starter-validation` não é necessário** — o Validation Plus já inclui Jakarta Validation + Hibernate Validator. **`spring-boot-starter-web` só se sua API for REST com Spring MVC** (`@RestController`, `@RequestBody`): o Validation Plus não o instala; na prática a maioria dos projetos web já o tem.

### 2. Anote seu DTO

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

> Todos os blocos Java deste README incluem **imports completos** para que você possa copiar e colar sem dúvidas de origem.

### 3. Valide no controller

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

O starter configura automaticamente o `Validator` com i18n e, se habilitado, um `ControllerAdvice` que retorna erros em formato JSON unificado.

## Configuração

Propriedades disponíveis em `application.properties`:

```properties
# Ativa/desativa a integração (default: true)
spring.validation-plus.enabled=true

# Ativa o ValidationExceptionHandler incluído (default: true)
spring.validation-plus.exception-handler.enabled=true

# Idioma padrão quando não há header Accept-Language
spring.web.locale=es
spring.web.locale-resolver=accept_header
```

| Propriedade | Default | Descrição |
|-----------|---------|-------------|
| `spring.validation-plus.enabled` | `true` | Validador, integração Spring e checkers JPA (`@Unique` / `@Exists`) |
| `spring.validation-plus.exception-handler.enabled` | `true` | `ValidationExceptionHandler` para erros 400 em JSON |

Se sua app já tem um `@RestControllerAdvice` próprio para validação, desative o handler da biblioteca e delegue apenas os erros de negócio (404, 401, etc.) ao seu advice local.

## Guia de uso

### Imports de referência

| Origem | Import típico | Quando |
|--------|---------------|--------|
| Validation Plus | `import dev.benjaminor.validationplus.constraints.*;` | `@Required`, `@EmailAddress`, `@Confirmed`, … |
| Jakarta Validation | `import jakarta.validation.Valid;` | `@Valid` em body, listas aninhadas |
| Spring Web | `import org.springframework.web.bind.annotation.*;` | Controllers REST |
| Spring (path params) | `import org.springframework.validation.annotation.Validated;` | `@MinValue` em `@PathVariable` |
| Operadores condicionais | `import dev.benjaminor.validationplus.constraints.ConditionalOperator;` | `operator` em `@RequiredIf`, `@ProhibitedIf`, … |

Todos os exemplos Java abaixo incluem os imports necessários.

### Padrão recomendado por campo

Empilhe as anotações nesta ordem:

1. **Presença** — `@Required` ou `@Nullable`
2. **Tipo** — `@StringType`, `@IntegerType`, `@ArrayType`, etc.
3. **Regras de negócio** — `@MinLength`, `@MinValue`, `@EmailAddress`, etc.

```java
import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MaxValue;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.MinValue;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.StringType;

public class ExampleRequest {

    @Required          // 1. obrigatório
    @StringType        // 2. deve ser String
    @MinLength(2)      // 3. pelo menos 2 caracteres
    @MaxLength(50)     // 3. máximo 50 caracteres
    private String name;

    @IntegerType       // 2. deve ser inteiro Java
    @MinValue(0)        // 3. >= 0
    @MaxValue(100)     // 3. <= 100
    private Integer page;
}
```

| Tipo Java | Constraints de tipo | Constraints de intervalo / formato |
|-----------|---------------------|--------------------------------|
| `String` | `@StringType` | `@MinLength`, `@MaxLength`, `@EmailAddress`, `@Regex`, `@In`, … |
| `Integer`, `Long`, … | `@IntegerType` | `@MinValue`, `@MaxValue`, `@Between`, `@Gt`, `@Lt`, … |
| `Double`, `Float`, `BigDecimal` | `@DecimalType` | `@MinValue`, `@MaxValue`, `@Digits`, … |
| `Boolean` | `@BooleanType` | `@Accepted`, `@Declined` |
| `List`, `Set`, array | `@ArrayType` | `@Between` (tamanho), `@Size`, `@Distinct` |
| Datas (`LocalDate`, `String`) | `@Date`, `@DateFormat` | `@Before`, `@After`, `@BeforeOrEqual`, … |

> `@EmailAddress`, `@MinLength` e similares **ignoram valores null ou em branco**. Combine-os com `@Required` quando o campo for obrigatório.

### Campos opcionais (`@Nullable`)

Para campos que **não são obrigatórios** em updates parciais (senha, sobrenome, etc.):

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

| Valor no JSON | `@Nullable` | `@MinLength(6)` | Resultado |
|---------------|-------------|-----------------|-----------|
| Campo **omitido** | ✅ | — (null) | ✅ Passa |
| `"password": null` | ✅ | — (null) | ✅ Passa |
| `"password": ""` | ✅ | ❌ comprimento 0 | **400** — deve ter pelo menos 6 caracteres |
| `"password": "abc"` | ✅ | ❌ | **400** |
| `"password": "secret123"` | ✅ | ✅ | ✅ Passa |

**Regra prática:** se o cliente **não quer alterar** o campo, deve **omití-lo** ou enviar `null`. Uma string vazia `""` conta como valor presente e as regras de comprimento/formato se aplicam.

O mesmo vale para filtros opcionais em query params: `@Nullable` + `@EmailAddress` permite deixar `email` vazio na URL.

### Body JSON (`@RequestBody`)

Fluxo padrão para POST/PUT:

```
JSON → Jackson deserializa → @Valid executa constraints → controller
```

```java
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody UserCreateRequest request) { ... }
```

Se o JSON traz um tipo incorreto (`"size": "abc"`), o `ValidationExceptionHandler` traduz o erro do Jackson para uma mensagem i18n amigável.

### Query params e formulários (`@ModelAttribute`)

Para GET com filtros, paginação ou **POST** com `application/x-www-form-urlencoded`:

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

**Importante:** Spring converte os query params **antes** de executar `@Valid`.

| Cenário | O que falha | Quem responde |
|-----------|-----------|----------------|
| `?size=abc` (não numérico) | Conversão de tipo | `ValidationExceptionHandler` → `"O campo size deve ser um inteiro."` |
| `?size=0` (numérico inválido) | `@MinValue(1)` | Constraint de validação |
| `?email=foo` (email ruim) | `@EmailAddress` | Constraint de validação |

`@IntegerType` em um campo `Integer` **não intercepta** texto não numérico em query params; isso ocorre na fase de binding. Uma vez convertido corretamente, `@IntegerType` e `@MinValue` se aplicam.

### Path variables (`@Validated`)

Para validar parâmetros de rota ou de método (não DTOs), anote o controller com `@Validated` e use constraints no parâmetro:

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

Os erros são retornados como `{ "errors": { "id": ["..."] } }` com mensagens i18n (mesmo formato que no body).

### Arrays e listas

Há **dois níveis** de validação:

| Nível | O que você valida | Constraints |
|-------|-------------|-------------|
| O array em si | Tipo, quantidade de elementos, sem duplicados | `@ArrayType`, `@Between`, `@Size`, `@Distinct`, `@Required` |
| Cada elemento | Email, comprimento, campos próprios | **DTO filho + `@Valid`** (ver próxima seção) |

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
    @Between(min = 1, max = 20)   // entre 1 e 20 elementos
    @Distinct                     // sem repetidos
    private List<String> tags;
}
```

`@Between` funciona sobre números, comprimento de strings **e tamanho de coleções/arrays**.

### Validação aninhada com DTOs

Quando cada item do array é um objeto com regras próprias, use um DTO filho e `@Valid` do Jakarta:

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

Erros aninhados na resposta:

```json
{
  "errors": {
    "items[0].quantity": ["O campo quantity deve ser pelo menos 1."],
    "items": ["O campo items deve ter entre 1 e 50 elementos."]
  }
}
```

> Os constraints do validation-plus **ainda não suportam** anotações inline em generics (`List<@EmailAddress String>`). Para validar cada string de uma lista, use um DTO wrapper ou um DTO filho com `@Valid`.

### Regras entre campos (cross-field)

Constraints que relacionam vários campos do mesmo DTO. Você pode colocá-los **no campo que é validado** (recomendado, estilo Laravel) ou **na classe** (forma clássica).

#### No campo (recomendado)

Coloque a anotação diretamente sobre o campo que deve cumprir a regra. O parâmetro indica o(s) campo(s) observado(s):

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

| Constraint | Sintaxe no campo |
|---|---|
| `@RequiredWith` / `@RequiredWithout` | `@RequiredWith("campoObservado")` |
| `@RequiredWithAll` / `@RequiredWithoutAll` | `@RequiredWithAll({"a", "b"})` |
| `@RequiredIf` / `@RequiredUnless` | `@RequiredIf(field = "role", value = "ADMIN")` |
| Operador condicional (`operator`) | `@RequiredIf(field = "role", value = "GUEST", operator = ConditionalOperator.NOT_EQUALS)` |
| Vários valores (`IN`) | `@RequiredIf(field = "role", value = "ADMIN,MODERATOR", operator = ConditionalOperator.IN)` |
| `@RequiredIfAccepted` / `@RequiredIfDeclined` | `@RequiredIfAccepted("termsAccepted")` |
| `@Same` / `@Different` | `@Same("password")` |
| `@Confirmed` | `@Confirmed("password")` ou `@Confirmed` se o campo termina em `Confirmation` |
| `@ProhibitedIf` / `@ProhibitedUnless` | `@ProhibitedIf(field = "role", value = "ADMIN")` |
| `@MissingIf` / `@MissingUnless` | `@MissingIf(field = "role", value = "GUEST")` |
| `@MissingWith` / `@MissingWithAll` | `@MissingWith("email")` |

> Em constraints que observam vários campos, `value` é alias de `fields`: `@RequiredWith({"email", "phone"})`.

Os constraints condicionais (`@RequiredIf`, `@RequiredUnless`, `@ProhibitedIf`, `@ProhibitedUnless`, `@MissingIf`, `@MissingUnless`) comparam `field` com `value` usando `operator` (padrão `ConditionalOperator.EQUALS`):

| Operador | Significado |
|----------|-------------|
| `EQUALS` (padrão) | O campo observado é igual a `value` |
| `NOT_EQUALS` | O campo observado é diferente de `value` |
| `IN` | O campo observado coincide com algum dos valores separados por vírgula em `value` |

`@RequiredIfAccepted` / `@RequiredIfDeclined` não usam `operator` (avaliam aceitação booleana do campo observado).

#### Na classe (alternativa)

A sintaxe clássica continua suportada quando você prefere centralizar as regras:

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

Ver [Referência de constraints → Entre campos](#entre-campos).

### Banco de dados (`@Unique`, `@Exists`)

Validações que consultam persistência em runtime. Requerem um **checker** registrado via SPI.

> **JPA não vem por padrão:** nem Spring Boot nem Validation Plus instalam JPA automaticamente. Adicione `spring-boot-starter-data-jpa` (mais driver e DataSource) seguindo [JPA para `@Unique` e `@Exists`](#jpa-para-unique-e-exists), ou registre checkers custom.

#### Checklist de integração JPA

1. Dependência `spring-boot-starter-data-jpa` no seu projeto (além do starter do validation-plus).
2. Entidade JPA com `@Entity` (ex.: `User.class`).
3. Constraint `@Unique` ou `@Exists` a **nível classe** do DTO, apontando para o campo do DTO e o atributo JPA.
4. Em **updates**, usar `excludeParameter` ou `excludeField` para não comparar contra o próprio registro.

O starter registra automaticamente `JpaUniquenessChecker` e `JpaExistenceChecker` quando detecta um `EntityManagerFactory` (após a auto-config do Hibernate). As consultas de banco de dados são executadas em transação somente leitura, também com `spring.jpa.open-in-view=false`.

#### Checkers custom (Spring Boot)

Declare um bean em vez dos defaults JPA — o starter o registra automaticamente:

```java
import dev.benjaminor.validationplus.spi.UniquenessChecker;
import org.springframework.context.annotation.Bean;

@Bean
UniquenessChecker uniquenessChecker(UserRepository repository) {
    return request -> repository.countByEmail((String) request.value()) == 0;
}
```

Se existir um bean `UniquenessChecker` ou `ExistenceChecker`, a implementação JPA não é criada.

#### Parâmetros de `@Unique`

| Parâmetro | Descrição |
|-----------|-------------|
| `entity` | Classe `@Entity` consultada |
| `field` | Campo do **DTO** validado e onde o erro é reportado |
| `column` | **Atributo JPA** usado em JPQL (`e.email`, não necessariamente o nome físico da coluna SQL) |
| `excludeField` | Campo do DTO com o id a excluir (alternativa a `excludeParameter`) |
| `excludeParameter` | Nome do `@PathVariable` ou query param com o id (ex.: `"id"` em `PUT /users/{id}`) |
| `excludeColumn` | Atributo identificador da entidade (padrão: `"id"`) |
| `ignoreCase` | Comparação case-insensitive para strings (padrão: `true`) |
| `message` | Mensagem personalizada (suporta i18n com chaves `{...}`) |

#### Criar — valor único

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

#### Atualizar — excluir o registro atual

`excludeParameter` lê o id da requisição HTTP atual (path variable) via `RequestContextValueProvider`:

```java
import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.Unique;

@Unique(
    entity = User.class,
    field = "email",
    column = "email",
    excludeParameter = "id",
    message = "O email já está registrado por outro usuário."
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
//                                    ↑ deve coincidir com excludeParameter = "id"
```

#### `@Exists` — o registro deve existir

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

Com **vários** `EntityManagerFactory` (multi-datasource), informe o persistence unit
(o mesmo nome de `LocalContainerEntityManagerFactoryBean#setPersistenceUnitName`
ou o bean name). Vazio = EMF `@Primary` / único:

```java
@Exists(entity = Registro.class, field = "idRegistro", column = "idRegistro",
        persistenceUnit = "nomina")
@Exists(entity = Estatus.class, field = "idEstatus", column = "idEstatus",
        persistenceUnit = "nomina")
public class PersistenciaEstatusRequest { ... }
```

`@Unique` aceita o mesmo atributo `persistenceUnit`.

#### Backend sem JPA (SPI manual)

Fora do Spring Boot, ou se preferir registro explícito, use o registro SPI na inicialização:

```java
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;

ValidationPlusCheckers.registerUniquenessChecker(request -> {
    // return true se o valor for único
});
ValidationPlusCheckers.registerExistenceChecker(request -> {
    // return true se o registro existir
});
```

Para valores de contexto HTTP (path variables), registre um `ContextValueProvider`. Em apps web Spring Boot, `RequestContextValueProvider` vem incluído por padrão; você pode substituí-lo pelo seu próprio `@Bean ContextValueProvider`.

#### Erros frequentes de `@Unique`

| Mensagem | Causa | Solução |
|---------|-------|----------|
| `Não há um verificador de unicidade configurado para {field}` | Não há checker registrado | Adicione `spring-boot-starter-data-jpa` ou registre um `UniquenessChecker` manual |
| O email existe mas é o **mesmo** registro no update | Falta excluir o id atual | Use `excludeParameter = "id"` alinhado com `@PathVariable` |
| Erro JPA / entidade não encontrada | `entity` ou `column` incorretos | `column` = nome do **atributo** na entidade Java |

## Resposta de erros

Formato JSON estilo Laravel, retornado por `ValidationExceptionHandler`:

```json
{
  "errors": {
    "email": ["O campo email é obrigatório."],
    "size": ["O campo size deve ser um inteiro."]
  }
}
```

O handler cobre:

- `MethodArgumentNotValidException` / `BindException` — body JSON, query params e campos de formulário
- `MethodArgumentTypeMismatchException` — path ou query params com tipos incompatíveis
- `HttpMessageNotReadableException` — JSON malformado ou tipos incorretos no body
- `HandlerMethodValidationException` / `ConstraintViolationException` — `@Validated` em métodos do controller (path variables, parâmetros de método)

Os erros de conversão (`typeMismatch`) são traduzidos para i18n com `FieldErrorMessageResolver` e `TypeMismatchMessageUtils` (`dev.benjaminor.validationplus.type.integer`, etc.).

## Internacionalização (i18n)

Mensagens incluídas no core:

| Arquivo | Idioma |
|---------|--------|
| `ValidationMessages.properties` | Inglês (padrão) |
| `ValidationMessages_es.properties` | Espanhol |
| `ValidationMessages_pt.properties` | Português |

**Seleção de idioma:**

1. Header `Accept-Language: es`, `Accept-Language: pt` ou `Accept-Language: en`
2. Fallback: `spring.web.locale=es` (Spring Boot)

**Sobrescrever mensagens** na sua app — crie `src/main/resources/ValidationMessages_es.properties` (ou o locale que usar). Defina apenas as chaves que quiser alterar; o resto faz fallback para o bundle da biblioteca.

```properties
dev.benjaminor.validationplus.constraints.Required.message=O campo {field} é obrigatório.
```

Para um único campo, use `message` na anotação:

```java
import dev.benjaminor.validationplus.constraints.Required;

public class ExampleRequest {

    @Required(message = "O nome é obrigatório")
    private String name;
}
```

**Templates de mensagens** (copie chaves dos arquivos incluídos):

- [ValidationMessages.properties](spring-validation-plus-core/src/main/resources/ValidationMessages.properties) — inglês
- [ValidationMessages_es.properties](spring-validation-plus-core/src/main/resources/ValidationMessages_es.properties) — espanhol
- [ValidationMessages_pt.properties](spring-validation-plus-core/src/main/resources/ValidationMessages_pt.properties) — português

**Placeholders disponíveis:** `{field}`, `{other}`, `{value}`, `{values}`, `{min}`, `{max}`, `{condition}`, `{format}`, `{integer}`, `{fraction}`, `{validatedValue}`

| Placeholder | Uso típico |
|---|---|
| `{field}` | Campo onde o erro é reportado |
| `{other}` | Campo(s) observado(s) em regras entre campos (ex.: o companion em `@RequiredWith`) |
| `{value}` | Valor disparador em regras condicionais (`@RequiredIf`, `@ProhibitedIf`, …); listas CSV aparecem como `A, B` |
| `{values}` | Listas de `@In` / MIME / extensões |
| `{min}` / `{max}` | Limites numéricos ou de tamanho |
| `{condition}` | Frase localizada do operador (`é` / `não é` / `é um de`, …) |
| `{format}` | Padrão de `@DateFormat` |
| `{integer}` / `{fraction}` | Dígitos de `@Digits` |

Os placeholders `{field}` e `{other}` são resolvidos pelo interpolador incluído (`ValidationPlusMessageInterpolator`). Se você os ver sem substituir (ou o nome do campo vazio), use o starter **≥ 0.3.3** e verifique que não definiu um `LocalValidatorFactoryBean` custom sem esse interpolador.

## Handler de exceções

Por padrão, `ValidationExceptionHandler` é registrado como `@RestControllerAdvice`. Para usar apenas o validador e seu próprio advice:

```properties
spring.validation-plus.exception-handler.enabled=false
```

Seu advice deve tratar pelo menos `MethodArgumentNotValidException` e `BindException` se quiser respostas 400 personalizadas. Os erros de conversão de query params (`typeMismatch` em `FieldError`) são traduzidos internamente por `FieldErrorMessageResolver` quando o handler da biblioteca está ativo.

## Arquitetura de módulos

```text
spring-validation-plus/
├── spring-validation-plus-core/                 # Publicável. Sem Spring Boot.
│   ├── constraints/                             # @Required, @EmailAddress, @Unique, …
│   ├── validators/                              # Implementações Jakarta Validation
│   ├── support/                                 # i18n, utilitários
│   ├── spi/                                     # UniquenessChecker, ExistenceChecker, …
│   └── resources/ValidationMessages*.properties
│
├── spring-validation-plus-spring-boot-starter/  # Publicável. Auto-config Spring Boot.
│   ├── autoconfigure/                           # Validator, JPA checkers, locale
│   ├── exception/                               # ValidationExceptionHandler
│   └── jpa/                                     # JpaUniquenessChecker, JpaExistenceChecker
│
└── spring-validation-plus-example/              # Não publicável. Referência executável.
    └── README.md                                # Mapa DTO → padrão → curl
```

| Artefato Maven | Quando usar |
|-----------------|---------------|
| `spring-validation-plus-spring-boot-starter` | Apps Spring Boot (recomendado) |
| `spring-validation-plus-core` | Projetos Jakarta Validation sem Spring, ou frameworks custom |

**Auto-configuração incluída no starter:**

| Classe | Responsabilidade |
|-------|-----------------|
| `SpringValidationPlusAutoConfiguration` | `LocalValidatorFactoryBean` com i18n, `ValidationExceptionHandler`, `RequestContextValueProvider` |
| `JpaValidationPlusAutoConfiguration` | `JpaUniquenessChecker` + `JpaExistenceChecker` (se houver JPA) |

## Referência executável (example)

O módulo **`spring-validation-plus-example`** é documentação **viva**: código executável + guia com curls.

```bash
docker compose up example   # http://localhost:8080
```

Consulte **[spring-validation-plus-example/README.es.md](spring-validation-plus-example/README.es.md)** — inclui:

- Mapa **DTO → padrão → endpoint**
- Exemplos de `@Unique`, `@ModelAttribute`, `@Valid` aninhado, `@RequiredIf`, `@Confirmed`
- H2 em memória para testar regras de banco de dados sem instalar nada extra

## Solução de problemas

### As mensagens saem em inglês

Postman e muitos clientes **não enviam** `Accept-Language`. Configure idioma padrão:

```properties
spring.web.locale=es
spring.web.locale-resolver=accept_header
```

Ou envie o header `Accept-Language: es` em cada requisição.

### Query param numérico inválido mostra erro em inglês

Ative o handler da biblioteca:

```properties
spring.validation-plus.exception-handler.enabled=true
```

Se você usa seu próprio `@RestControllerAdvice`, deve traduzir erros `typeMismatch` em `FieldError` ou reutilizar a lógica de `FieldErrorMessageResolver`.

### `@Unique` responde "Não há um verificador de unicidade configurado"

1. Confirme `spring-boot-starter-data-jpa` no classpath.
2. Confirme que a app inicia com JPA (DataSource + entidades).
3. Use versão do starter que inclui `JpaValidationPlusAutoConfiguration` (ativa **depois** do Hibernate).
4. Sem JPA: implemente e registre `UniquenessChecker` manualmente (ver [SPI custom](#backend-sem-jpa-spi-manual)).

### `@MinLength` falha em um campo que quero deixar vazio

`""` **não é** o mesmo que `null`. Omita o campo do JSON ou envie `"campo": null`. Ver [Campos opcionais](#campos-opcionais-nullable).

### Validação aninhada não roda em uma lista

Falta `@Valid` do Jakarta na coleção:

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

### Quero desativar apenas o handler, não o validador

```properties
spring.validation-plus.exception-handler.enabled=false
spring.validation-plus.enabled=true
```

## Referência de constraints

### Campo

| Constraint | Descrição |
|---|---|
| `@Required` | Campo obrigatório (não null, não vazio) |
| `@Nullable` | Documenta que pode ser null; nunca falha |
| `@Filled` | Não vazio se presente |
| `@StringType` | Deve ser `String` / `CharSequence` |
| `@IntegerType` | Deve ser inteiro Java (`Integer`, `Long`, `Byte`, …) |
| `@DecimalType` | `Float`, `Double` ou `BigDecimal` |
| `@BooleanType` | `Boolean` real |
| `@ArrayType` | Array ou `Collection` |
| `@Numeric` | Qualquer `Number` |
| `@EmailAddress` | Email válido (ignora blank) |
| `@MinLength` / `@MaxLength` | Comprimento mínimo/máximo |
| `@MinValue` / `@MaxValue` | Valor numérico mínimo/máximo |
| `@Between` | Intervalo numérico, comprimento de texto ou tamanho de coleção |
| `@Size` | Tamanho exato (texto, coleção ou número) |
| `@Accepted` / `@Declined` | Valores truthy/falsy: `true`/`false`, `yes`/`no`, `on`/`off`, `1`/`0`, strings `true`/`false`, `T`/`F` (sem distinguir maiúsculas) |
| `@In` / `@NotIn` | Valor dentro/fora de uma lista |
| `@Regex` / `@NotRegex` | Coincide/não coincide com padrão |
| `@Url` | URL válida |
| `@ActiveUrl` | URL http(s) com host (sem DNS/HTTP) |
| `@Uuid` | UUID válido |
| `@Ulid` | ULID válido |
| `@HexColor` | Cor hexadecimal |
| `@Date` | Data válida (temporal ou string) |
| `@DateFormat` | Data com formato obrigatório |
| `@Before` / `@After` | Data anterior/posterior a outra |
| `@BeforeOrEqual` / `@AfterOrEqual` | Data anterior/posterior inclusive |
| `@Digits` | Dígitos inteiros e decimais exatos |
| `@MinDigits` / `@MaxDigits` / `@DigitsBetween` | Quantidade de dígitos |
| `@StartsWith` / `@EndsWith` | Prefixo/sufixo de texto |
| `@NotStartsWith` / `@NotEndsWith` | Não começa/termina com texto |
| `@Json` | String JSON válida |
| `@Alpha` / `@AlphaNum` / `@AlphaDash` | Só letras, alfanumérico, alfanumérico com hífens |
| `@Ascii` | Só caracteres ASCII |
| `@Lowercase` / `@Uppercase` | Minúsculas / maiúsculas |
| `@Ip` / `@Ipv4` / `@Ipv6` / `@MacAddress` | Endereços de rede |
| `@Timezone` | Fuso horário válido |
| `@Gt` / `@Gte` / `@Lt` / `@Lte` | Comparações numéricas estritas |
| `@MultipleOf` | Múltiplo de um número |
| `@EnumValue` | Valor de um enum Java |
| `@Password` | Política de senha configurável (`min`, `letters`, `numbers`, …) |
| `@Distinct` | Valores únicos em array/coleção |
| `@File` | Arquivo enviado (`MultipartFile`, `Part`) com tamanho/tipo opcional |
| `@Image` | Imagem enviada com dimensões e tamanho opcionais |

### Entre campos (cross-field)

Suportam nível **campo** (recomendado) e nível **classe**. Ver [Regras entre campos](#regras-entre-campos-cross-field).

| Constraint | Descrição |
|---|---|
| `@RequiredIf` | Obrigatório se outro campo cumprir a condição (`operator`: `EQUALS`, `NOT_EQUALS`, `IN`) |
| `@RequiredUnless` | Obrigatório salvo se outro campo cumprir a condição |
| `@RequiredWith` / `@RequiredWithout` | Obrigatório se algum companion estiver presente/ausente |
| `@RequiredWithAll` / `@RequiredWithoutAll` | Obrigatório se todos/ausência total de companions |
| `@RequiredIfAccepted` / `@RequiredIfDeclined` | Obrigatório se campo aceito/recusado |
| `@Same` / `@Different` | Dois campos devem coincidir / ser distintos |
| `@Confirmed` | Deve coincidir com `{field}Confirmation` (ou campo indicado) |
| `@Prohibited` / `@ProhibitedIf` / `@ProhibitedUnless` | Campo proibido (condicional) |
| `@Missing` / `@MissingIf` / `@MissingUnless` | Campo deve estar ausente (condicional) |
| `@MissingWith` / `@MissingWithAll` | Ausente se companion(s) presente(s) |
| `@InArray` | Valor deve existir em outro campo array/coleção |

### Banco de dados (nível classe, SPI)

| Constraint | Descrição |
|---|---|
| `@Unique` | Valor único em tabela/entidade |
| `@Exists` | Registro deve existir em tabela/entidade |

> `@Unique` e `@Exists` são `@Repeatable`: você pode declarar várias regras no mesmo DTO.

## Desenvolvimento

```text
spring-validation-plus/
├── spring-validation-plus-core/
├── spring-validation-plus-spring-boot-starter/
└── spring-validation-plus-example/    ← referência executável (ver README.es.md)
```

```bash
# Compilar e executar todos os testes
docker compose run --rm maven

# Só testes do core
docker compose run --rm maven mvn -pl spring-validation-plus-core test

# App de referência (porta 8080)
docker compose up example

# Instalar no .m2 local (ex.: para testar um SNAPSHOT de main)
docker compose run --rm maven mvn clean install
```

Para compilar versões não publicadas a partir do código fonte, clone o repo e execute `mvn clean install` localmente. Os releases são publicados no Maven Central — ver [PUBLISHING.md](PUBLISHING.md) (mantenedores).

## Roadmap

- Suporte `TYPE_USE` em constraints (`List<@EmailAddress String>`)
- Melhorias de multipart em `ValidationExceptionHandler`
- `autoPublish=true` no Central Portal quando a automação de releases estiver estável

## Licença

Copyright © 2026 **Benjamín Olvera R.**

Licensed under the [Apache License, Version 2.0](LICENSE).
