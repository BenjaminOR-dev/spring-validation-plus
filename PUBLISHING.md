# Publicar en Maven Central

Guía para publicar `spring-validation-plus-core` y `spring-validation-plus-spring-boot-starter` en [Maven Central](https://central.sonatype.com/).

Opciones de deploy: **Docker** (`.env` local) o **GitHub Actions** (tags `v*` — ver [.github/workflows/release.yml](.github/workflows/release.yml)).

## Qué se publica

| Artefacto | ¿Se publica? |
|-----------|--------------|
| `spring-validation-plus-core` | Sí |
| `spring-validation-plus-spring-boot-starter` | Sí |
| `spring-validation-plus` (POM padre) | Sí |
| `spring-validation-plus-example` | **No** (excluido con `-pl core,starter -am` en el deploy) |

## Requisitos (una sola vez)

### 1. Repositorio en GitHub

Repo público: `https://github.com/BenjaminOR-dev/spring-validation-plus`

Namespace Maven verificado: **`io.github.benjaminor-dev`** (Central Portal → pestaña Namespace).

### 2. Namespace en Central Portal

Ya verificado vía GitHub como **`io.github.benjaminor-dev`**. El `groupId` del proyecto debe coincidir — no hace falta registrar `dev.benjaminor` salvo que tengas ese dominio y lo verifiques aparte.

### 3. Clave GPG

```bash
gpg --full-generate-key
gpg --list-secret-keys --keyid-format long
gpg --keyserver keys.openpgp.org --send-keys TU_KEY_ID
```

### 4. Credenciales locales (deploy con Docker)

Copia [.env.example](.env.example) a `.env` (gitignored) y rellena:

| Variable | Origen |
|----------|--------|
| `CENTRAL_USERNAME` / `CENTRAL_PASSWORD` | [Central Portal → User Token](https://central.sonatype.com/usertoken) |
| `GPG_KEY_ID` | `gpg --list-secret-keys --keyid-format long` |
| `GPG_PASSPHRASE` | Passphrase de tu clave GPG |

Backup de clave privada GPG: `.local/gpg-signing-private.asc` (ver [PUBLISHING.local.md](PUBLISHING.local.md) si lo tienes en local).

Para flujos sin Docker, puedes usar [docs/settings-central.xml.example](docs/settings-central.xml.example) en `~/.m2/settings.xml`.

> **Seguridad — nunca commitees:**
>
> - `.env` (credenciales reales)
> - `.local/` (material de claves GPG)
> - Tokens de Central Portal o passphrases GPG en el repositorio

## Checklist de release

### 1. Versión de release (sin SNAPSHOT)

**Todo en un solo commit**, antes de crear el tag. Si falta alguno, el tag quedará desalineado (como pasó en `0.2.0` con los README).

#### POMs (4 archivos)

| Archivo | Qué cambiar |
|---------|-------------|
| `pom.xml` (raíz) | `<version>` y `<scm><tag>vX.Y.Z</tag></scm>` |
| `spring-validation-plus-core/pom.xml` | `<version>` del parent |
| `spring-validation-plus-spring-boot-starter/pom.xml` | `<version>` del parent |
| `spring-validation-plus-example/pom.xml` | `<version>` del parent |

```xml
<version>0.3.1</version>
```

```xml
<scm>
    ...
    <tag>v0.3.1</tag>
</scm>
```

#### README (3 archivos — sección **Inicio rápido → Dependency**)

En cada uno actualiza **Maven**, **Gradle Kotlin** y **Gradle Groovy**:

| Archivo | Líneas típicas |
|---------|----------------|
| `README.md` | `<version>…</version>` + `implementation(...)` × 2 |
| `README.es.md` | igual |
| `README.pt.md` | igual |

Ejemplo:

```xml
<version>0.3.1</version>
```

```kotlin
implementation("io.github.benjaminor-dev:spring-validation-plus-spring-boot-starter:0.3.1")
```

#### Opcional (recomendado)

| Dónde | Qué |
|-------|-----|
| `PUBLISHING.md` → *Dependencia para consumidores* | Ejemplos Maven/Gradle con la nueva versión |
| `demo-springboot/pom.xml` (repo externo) | Dependencia del starter en Central |

#### Verificación rápida

```bash
grep -r "0.3.0-SNAPSHOT\|0.4.0-SNAPSHOT" pom.xml */pom.xml   # no debe quedar SNAPSHOT
grep "spring-validation-plus-spring-boot-starter:0\." README*.md  # misma versión en los 3
```

Maven Central no acepta `-SNAPSHOT` en el repositorio de releases.

### 2. Ejecutar tests

```bash
docker compose run --rm maven mvn clean verify
```

### 3. Deploy con perfil `release`

Asegúrate de tener `.env` (desde `.env.example`) y la clave GPG en `.local/`.

```bash
docker compose run --rm maven ./docker/deploy-release.sh
```

El script lee `.env`, genera `settings.xml` en el contenedor, importa la clave desde `.local/` y ejecuta:

```bash
mvn clean deploy -Prelease -pl spring-validation-plus-core,spring-validation-plus-spring-boot-starter -am
```

Los flags `-pl … -am` publican solo **core**, **starter** y el POM padre. El módulo **example** se compila en local pero **no** se sube a Maven Central.

Notas:

- No montes `~/.gnupg` de macOS — `gpg-agent` falla en Docker.
- El perfil `release` usa GPG en modo loopback (sin agent).
- La caché Maven usa el volumen `.m2/` del proyecto en `docker-compose.yml`.

Alternativa: ejecutar el deploy **en el host** (fuera de Docker) si tienes Maven y GPG locales:

```bash
export GPG_TTY=$(tty)
mvn clean deploy -Prelease \
  -pl spring-validation-plus-core,spring-validation-plus-spring-boot-starter \
  -am
```

### 4. Release con GitHub Actions (opcional)

Push de un tag de versión (`v0.2.0`, etc.) cuando el POM ya esté en versión **release** (sin `-SNAPSHOT`). Workflow: [.github/workflows/release.yml](.github/workflows/release.yml).

Configura estos [secrets del repositorio](https://docs.github.com/en/actions/security-for-github-actions/security-guides/using-secrets-in-github-actions):

| Secret | Valor |
|--------|-------|
| `CENTRAL_USERNAME` | Username del user token de Central Portal |
| `CENTRAL_PASSWORD` | Password del user token de Central Portal |
| `GPG_KEY_ID` | Id de clave GPG (formato long) |
| `GPG_PASSPHRASE` | Passphrase de la clave GPG |
| `GPG_PRIVATE_KEY` | Clave privada armored (contenido de `.local/gpg-signing-private.asc`) |

El workflow verifica que el POM no sea SNAPSHOT, importa GPG y publica solo **core**, **starter** y el POM padre — igual que `deploy-release.sh`.

> **Nota:** `0.1.0` en Maven Central incluyó por error `spring-validation-plus-example`. Releases **`0.2.0+`** lo excluyen.

### 5. Publicar en Central Portal

Con `autoPublish=false` (config actual), tras el upload:

1. Abre [central.sonatype.com](https://central.sonatype.com/) → tu deployment.
2. Revisa la validación.
3. Pulsa **Publish**.

En unos minutos debería aparecer en [search.maven.org](https://search.maven.org/).

### 6. Tag en Git

El tag debe apuntar al **commit del paso 1** (POMs + README + `<scm><tag>` ya incluidos):

```bash
git tag -a v0.3.1 -m "Release 0.3.1"
git push origin v0.3.1
```

> Si el workflow Release está activo, el push del tag dispara el deploy. Desactiva el workflow primero si solo mueves el tag sin republicar (ver nota en conversación de releases).

### 7. Subir versión de desarrollo

En los **4 POMs** (mismo listado del paso 1), commit separado en `main`:

```xml
<version>0.4.0-SNAPSHOT</version>
```

Los README **no** cambian aquí — siguen mostrando la última versión publicada en Central.

## Dependencia para consumidores (tras publicar)

**Maven**

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>0.3.1</version>
</dependency>
```

**Gradle**

```kotlin
implementation("io.github.benjaminor-dev:spring-validation-plus-spring-boot-starter:0.3.1")
```

Sin repositorios extra.

## Problemas frecuentes

| Problema | Solución |
|----------|----------|
| `401 Unauthorized` en deploy | Revisa token en `~/.m2/settings.xml`, server id = `central` |
| Falla firma GPG | Verifica `gpg.keyname`, monta `~/.gnupg`, o `export GPG_TTY=$(tty)` |
| Namespace no permitido | Confirma `groupId` = `io.github.benjaminor-dev` y namespace **Verified** en Central Portal |
| Errores de Javadoc | `failOnError=false` en el perfil release; mejora docs después |
| Se subió el example | El deploy usa `-pl core,starter -am`; example queda excluido de Central |

## Pendiente

- [x] Configurar secrets de GitHub Actions y validar un release por tag (`v0.2.0`)
- [ ] `autoPublish=true` cuando los releases automatizados estén estables
