# Spring Validation Plus

[🇬🇧 English version](README.md) | [🇪🇸 Versión en español](README.es.md)

[![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%7C%204.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.x-blue)](https://jakarta.ee/specifications/bean-validation/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter?label=Maven%20Central)](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Validações inspiradas no **Laravel** para **Spring Boot** e **Jakarta Validation**.

O Spring Validation Plus adiciona mais de **85 constraints** personalizados que funcionam como os nativos do Jakarta Validation: você os anota nos seus DTOs e eles são executados com `@Valid` ou `@Validated`. Inclui mensagens i18n (pt/en/es), tratamento unificado de erros JSON e suporte opcional para regras de banco de dados (`@Unique`, `@Exists`).

**Inclui:**

- Constraints estilo Laravel (`@Required`, `@EmailAddress`, `@Confirmed`, `@RequiredIf`, …)
- Validação de tipos (`@StringType`, `@IntegerType`, `@ArrayType`, …)
- Respostas JSON `{ "errors": { "campo": ["mensagem"] } }`
- Tradução automática de erros de conversão (query params, JSON com tipo incorreto)
- Integração JPA opcional para `@Unique` e `@Exists`
- Core utilizável sem Spring Boot (`spring-validation-plus-core`)

## Índice

- [Requisitos](#requisitos)
- [Início rápido](#início-rápido)
- [Configuração](#configuração)
- [Guia de uso](#guia-de-uso)
  - [Padrão recomendado por campo](#padrão-recomendado-por-campo)
  - [Campos opcionais (`@Nullable`)](#campos-opcionais-nullable)
  - [Body JSON (`@RequestBody`)](#body-json-requestbody)
  - [Query params e formulários (`@ModelAttribute`)](#query-params-e-formulários-modelattribute)
  - [Path variables (`@Validated`)](#path-variables-validated)
  - [Arrays e listas](#arrays-e-listas)
  - [Validação aninhada com DTOs](#validação-aninhada-com-dtos)
  - [Regras em nível de classe](#regras-em-nível-de-classe)
  - [Banco de dados (`@Unique`, `@Exists`)](#banco-de-dados-unique-exists)
- [Resposta de erros](#resposta-de-erros)
- [Internacionalização (i18n)](#internacionalização-i18n)
- [Handler de exceções](#handler-de-exceções)
- [Arquitetura de módulos](#arquitetura-de-módulos)
- [Referência executável (example)](#referência-executável-example)
- [Solução de problemas](#solução-de-problemas)
- [Referência de constraints](#referência-de-constraints)
- [Desenvolvimento](#desenvolvimento)
- [Roadmap](#roadmap)
- [Licença](#licença)

## Requisitos

- Java 17+
- Spring Boot 3.x / 4.x
- Jakarta Validation 3.x

## Início rápido

### 1. Dependência

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

**Multi-módulo Maven** (mesmo repositório):

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>${project.version}</version>
</dependency>
```

> Disponível no [Maven Central](https://search.maven.org/artifact/io.github.benjaminor-dev/spring-validation-plus-spring-boot-starter) — não é necessário configurar repositórios extras.

**Dependências opcionais**

| Você precisa | Adicione também |
|-----------|---------------|
| Apenas validação (sem Spring Boot) | `spring-validation-plus-core` |
| Regras `@Unique` / `@Exists` com JPA | `spring-boot-starter-data-jpa` |

### 2. Anote seu DTO

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

### 3. Valide no controller

```java
@PostMapping("/users")
public ResponseEntity<User> create(@Valid @RequestBody UserCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
}
```

O starter configura automaticamente o `Validator` com i18n e, se habilitado, um `ControllerAdvice` que retorna erros em formato JSON unificado.

> **Nota:** seu projeto deve incluir validação Jakarta (`spring-boot-starter-validation` ou `spring-boot-starter-web`, que já a traz transitivamente).

## Configuração

Propriedades disponíveis em `application.properties`:

```properties
# Activa/desactiva la integración (default: true)
spring.validation-plus.enabled=true

# Activa el ValidationExceptionHandler incluido (default: true)
spring.validation-plus.exception-handler.enabled=true

# Idioma por defecto cuando no hay header Accept-Language
spring.web.locale=es
spring.web.locale-resolver=accept_header
```

| Propriedade | Default | Descrição |
|-----------|---------|-------------|
| `spring.validation-plus.enabled` | `true` | Validador, integração Spring e checkers JPA (`@Unique` / `@Exists`) |
| `spring.validation-plus.exception-handler.enabled` | `true` | `ValidationExceptionHandler` para erros 400 em JSON |

Se sua app já possui um `@RestControllerAdvice` próprio para validação, desative o handler da biblioteca e delegue apenas os erros de negócio (404, 401, etc.) ao seu advice local.

## Guia de uso

### Padrão recomendado por campo

Empilhe as anotações nesta ordem:

1. **Presença** — `@Required` ou `@Nullable`
2. **Tipo** — `@StringType`, `@IntegerType`, `@ArrayType`, etc.
3. **Regras de negócio** — `@MinLength`, `@MinValue`, `@EmailAddress`, etc.

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
@Nullable
@StringType
@MinLength(6)
@MaxLength(255)
private String password;
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
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody UserCreateRequest request) { ... }
```

Se o JSON trouxer um tipo incorreto (`"size": "abc"`), o `ValidationExceptionHandler` traduz o erro do Jackson para uma mensagem i18n amigável.

### Query params e formulários (`@ModelAttribute`)

Para GET com filtros, paginação ou **POST** com `application/x-www-form-urlencoded`:

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

**Importante:** o Spring converte os query params **antes** de executar `@Valid`.

| Cenário | O que falha | Quem responde |
|-----------|-----------|----------------|
| `?size=abc` (não numérico) | Conversão de tipo | `ValidationExceptionHandler` → `"El campo size debe ser un entero."` |
| `?size=0` (numérico inválido) | `@MinValue(1)` | Constraint de validação |
| `?email=foo` (email inválido) | `@EmailAddress` | Constraint de validação |

`@IntegerType` em um campo `Integer` **não intercepta** texto não numérico em query params; isso ocorre na fase de binding. Uma vez convertido corretamente, `@IntegerType` e `@MinValue` se aplicam.

### Path variables (`@Validated`)

Para validar parâmetros de rota ou de método (não DTOs), anote o controller com `@Validated` e use constraints no parâmetro:

```java
@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public User find(@PathVariable @MinValue(1) Long id) { ... }
}
```

Os erros são retornados como `{ "errors": { "id": ["..."] } }` com mensagens i18n (mesmo formato do body).

### Arrays e listas

Há **dois níveis** de validação:

| Nível | O que você valida | Constraints |
|-------|-------------|-------------|
| O array em si | Tipo, quantidade de elementos, sem duplicados | `@ArrayType`, `@Between`, `@Size`, `@Distinct`, `@Required` |
| Cada elemento | Email, comprimento, campos próprios | **DTO filho + `@Valid`** (ver próxima seção) |

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

`@Between` funciona sobre números, comprimento de strings **e tamanho de coleções/arrays**.

### Validação aninhada com DTOs

Quando cada item do array é um objeto com regras próprias, use um DTO filho e `@Valid` do Jakarta:

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

Erros aninhados na resposta:

```json
{
  "errors": {
    "items[0].quantity": ["El campo quantity debe ser al menos 1.0."],
    "items": ["El campo items debe tener entre 1.0 y 50.0 elementos."]
  }
}
```

> Os constraints do validation-plus **ainda não suportam** anotações inline em generics (`List<@EmailAddress String>`). Para validar cada string de uma lista, use um DTO wrapper ou um DTO filho com `@Valid`.

### Regras em nível de classe

Constraints que relacionam vários campos do mesmo DTO:

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

Ver [Referência de constraints → Entre campos](#entre-campos-nível-classe).

### Banco de dados (`@Unique`, `@Exists`)

Validações que consultam persistência em runtime. Requerem um **checker** registrado via SPI.

#### Checklist de integração JPA

1. Dependência `spring-boot-starter-data-jpa` no seu projeto (além do starter do validation-plus).
2. Entidade JPA com `@Entity` (ex.: `User.class`).
3. Constraint `@Unique` ou `@Exists` a **nível de classe** do DTO, apontando para o campo do DTO e o atributo JPA.
4. Em **updates**, usar `excludeParameter` ou `excludeField` para não comparar contra o próprio registro.

O starter registra automaticamente `JpaUniquenessChecker` e `JpaExistenceChecker` quando detecta um `EntityManagerFactory` (após a auto-config do Hibernate). As consultas de banco de dados são executadas em transação somente leitura, inclusive com `spring.jpa.open-in-view=false`.

#### Checkers custom (Spring Boot)

Declare um bean em vez dos defaults JPA — o starter o registra automaticamente:

```java
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
| `excludeColumn` | Atributo identificador da entidade (default: `"id"`) |
| `ignoreCase` | Comparação case-insensitive para strings (default: `true`) |
| `message` | Mensagem personalizada (suporta i18n com chaves `{...}`) |

#### Criar — valor único

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

#### Atualizar — excluir o registro atual

`excludeParameter` lê o id da requisição HTTP atual (path variable) via `RequestContextValueProvider`:

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

#### `@Exists` — o registro deve existir

```java
@Exists(entity = Role.class, field = "roleId", column = "id")
public class AssignRoleRequest {

    @Required
    @IntegerType
    private Long roleId;
}
```

#### Backend sem JPA (SPI manual)

Fora do Spring Boot, ou se preferir registro explícito, use o registro SPI na inicialização:

```java
ValidationPlusCheckers.registerUniquenessChecker(request -> {
    // return true si el valor es único
});
ValidationPlusCheckers.registerExistenceChecker(request -> {
    // return true si el registro existe
});
```

Para valores de contexto HTTP (path variables), registre um `ContextValueProvider`. Em apps web Spring Boot, `RequestContextValueProvider` é incluído por padrão; você pode substituí-lo pelo seu próprio `@Bean ContextValueProvider`.

#### Erros frequentes de `@Unique`

| Mensagem | Causa | Solução |
|---------|-------|----------|
| `No hay un verificador de unicidad configurado para {field}` | Não há checker registrado | Adicione `spring-boot-starter-data-jpa` ou registre um `UniquenessChecker` manual |
| O email existe mas é o **mesmo** registro no update | Falta excluir o id atual | Use `excludeParameter = "id"` alinhado com `@PathVariable` |
| Erro JPA / entidade não encontrada | `entity` ou `column` incorretos | `column` = nome do **atributo** na entidade Java |

## Resposta de erros

Formato JSON estilo Laravel, retornado por `ValidationExceptionHandler`:

```json
{
  "errors": {
    "email": ["El campo email es obligatorio."],
    "size": ["El campo size debe ser un entero."]
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
| `ValidationMessages.properties` | Inglês (default) |
| `ValidationMessages_es.properties` | Espanhol |
| `ValidationMessages_pt.properties` | Português (pt / pt-BR) |

**Seleção de idioma:**

1. Header `Accept-Language: pt`, `Accept-Language: pt-BR`, `Accept-Language: es` ou `Accept-Language: en`
2. Fallback: `spring.web.locale=pt` (Spring Boot)

**Sobrescrever mensagens** na sua app — crie `src/main/resources/ValidationMessages_pt.properties` (ou o locale desejado). Defina apenas as chaves que quiser alterar; as demais usam fallback do bundle da biblioteca.

```properties
dev.benjaminor.validationplus.constraints.Required.message=O campo {field} é obrigatório.
```

Para um único campo, use `message` na anotação:

```java
@Required(message = "O nome é obrigatório")
private String name;
```

**Modelos de mensagens** (copie chaves dos arquivos incluídos):

- [ValidationMessages.properties](spring-validation-plus-core/src/main/resources/ValidationMessages.properties) — inglês
- [ValidationMessages_es.properties](spring-validation-plus-core/src/main/resources/ValidationMessages_es.properties) — espanhol
- [ValidationMessages_pt.properties](spring-validation-plus-core/src/main/resources/ValidationMessages_pt.properties) — português

**Placeholders disponíveis:** `{field}`, `{min}`, `{max}`, `{value}`, `{other}`, `{validatedValue}`, `{integer}`, `{fraction}`

## Handler de exceções

Por padrão, `ValidationExceptionHandler` é registrado como `@RestControllerAdvice`. Para usar apenas o validador e seu próprio advice:

```properties
spring.validation-plus.exception-handler.enabled=false
```

Seu advice deve tratar pelo menos `MethodArgumentNotValidException` e `BindException` se quiser respostas 400 personalizadas. Os erros de conversão de query params (`typeMismatch` em `FieldError`) são traduzidos internamente por `FieldErrorMessageResolver` quando o handler da biblioteca está ativo.

## Arquitetura de módulos

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

Consulte **[spring-validation-plus-example/README.md](spring-validation-plus-example/README.md)** — inclui:

- Mapa **DTO → padrão → endpoint**
- Exemplos de `@Unique`, `@ModelAttribute`, `@Valid` aninhado, `@RequiredIf`, `@Confirmed`
- H2 em memória para testar regras de banco de dados sem instalar nada extra

## Solução de problemas

### As mensagens aparecem em inglês

Postman e muitos clientes **não enviam** `Accept-Language`. Configure o idioma padrão:

```properties
spring.web.locale=es
spring.web.locale-resolver=accept_header
```

Ou envie o header `Accept-Language: pt` em cada requisição.

### Query param numérico inválido exibe erro em inglês

Ative o handler da biblioteca:

```properties
spring.validation-plus.exception-handler.enabled=true
```

Se você usa seu próprio `@RestControllerAdvice`, deve traduzir erros `typeMismatch` em `FieldError` ou reutilizar a lógica de `FieldErrorMessageResolver`.

### `@Unique` responde "No hay un verificador de unicidad configurado"

1. Confirme `spring-boot-starter-data-jpa` no classpath.
2. Confirme que a app inicia com JPA (DataSource + entidades).
3. Use versão do starter que inclui `JpaValidationPlusAutoConfiguration` (ativa **após** o Hibernate).
4. Sem JPA: implemente e registre `UniquenessChecker` manualmente (ver [SPI custom](#backend-sem-jpa-spi-manual)).

### `@MinLength` falha em um campo que quero deixar vazio

`""` **não é** o mesmo que `null`. Omita o campo do JSON ou envie `"campo": null`. Ver [Campos opcionais](#campos-opcionais-nullable).

### Validação aninhada não executa em uma lista

Falta `@Valid` do Jakarta na coleção:

```java
@Valid
private List<ItemRequest> items;
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
| `@Filled` | Não vazio se estiver presente |
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
| `@Regex` / `@NotRegex` | Corresponde/não corresponde a um padrão |
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
| `@Alpha` / `@AlphaNum` / `@AlphaDash` | Apenas letras, alfanumérico, alfanumérico com hífens |
| `@Ascii` | Apenas caracteres ASCII |
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

### Entre campos (nível classe)

| Constraint | Descrição |
|---|---|
| `@RequiredIf` | Obrigatório se outro campo tiver um valor |
| `@RequiredUnless` | Obrigatório salvo se outro campo tiver um valor |
| `@RequiredWith` / `@RequiredWithout` | Obrigatório se algum companion estiver presente/ausente |
| `@RequiredWithAll` / `@RequiredWithoutAll` | Obrigatório se todos/ausência total de companions |
| `@RequiredIfAccepted` / `@RequiredIfDeclined` | Obrigatório se campo aceito/recusado |
| `@Same` / `@Different` | Dois campos devem coincidir / ser distintos |
| `@Confirmed` | Deve coincidir com `{field}Confirmation` |
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
└── spring-validation-plus-example/    ← referência executável (ver README.md)
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

Para compilar versões não publicadas a partir do código-fonte, clone o repositório e execute `mvn clean install` localmente. Os releases são publicados no Maven Central — veja [PUBLISHING.md](PUBLISHING.md) (mantenedores).

## Roadmap

- Validar release automatizado por tag (secrets no GitHub — ver [PUBLISHING.md](PUBLISHING.md))
- Suporte `TYPE_USE` em constraints (`List<@EmailAddress String>`)
- Melhorias de multipart no `ValidationExceptionHandler`
- `autoPublish=true` no Central Portal quando a automação de releases estiver estável

## Licença

Copyright © 2026 **Benjamín Olvera R.**

Licensed under the [Apache License, Version 2.0](LICENSE).
