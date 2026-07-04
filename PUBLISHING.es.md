# Publicar en Maven Central

[🇬🇧 English version](PUBLISHING.md)

Guía para publicar **manualmente** `spring-validation-plus-core` y `spring-validation-plus-spring-boot-starter` en [Maven Central](https://central.sonatype.com/).

> **Pendiente:** workflow de GitHub Actions.

## Qué se publica

| Artefacto | ¿Se publica? |
|-----------|--------------|
| `spring-validation-plus-core` | Sí |
| `spring-validation-plus-spring-boot-starter` | Sí |
| `spring-validation-plus` (POM padre) | Sí |
| `spring-validation-plus-example` | **No** (`maven.deploy.skip=true`) |

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

### 4. Settings de Maven

Copia [docs/settings-central.xml.example](docs/settings-central.xml.example) en `~/.m2/settings.xml` (fusiona con lo que ya tengas):

- **Server `central`:** usuario + contraseña del token en Central Portal → **Generate User Token**
- **Profile `release`:** propiedad `gpg.keyname` = id de tu clave GPG

> **Seguridad — nunca commitees al repositorio:**
>
> - Tokens o contraseñas de Central Portal (solo en `~/.m2/settings.xml` en tu máquina)
> - Claves privadas GPG o el contenido de `~/.gnupg/`
> - Una copia de `settings.xml` ya rellenada con credenciales reales
>
> El archivo [docs/settings-central.xml.example](docs/settings-central.xml.example) es una **plantilla con placeholders** — es seguro tenerlo en el repo público.

## Checklist de release

### 1. Versión de release (sin SNAPSHOT)

En el `pom.xml` raíz y módulos:

```xml
<version>0.1.0</version>
```

Maven Central no acepta `-SNAPSHOT` en el repositorio de releases.

### 2. Ejecutar tests

```bash
docker compose run --rm maven mvn clean verify
```

### 3. Deploy con perfil `release`

```bash
docker compose run --rm \
  -v "$HOME/.m2:/root/.m2" \
  -v "$HOME/.gnupg:/root/.gnupg" \
  maven mvn clean deploy -Prelease
```

Notas:

- Monta `.m2` para credenciales de Central y settings GPG.
- Monta `.gnupg` si las claves están en el host.
- La imagen Docker incluye `gnupg` para firmar durante el deploy.
- Usa **`-it`** si GPG pide la passphrase de forma interactiva:

```bash
export GPG_TTY=$(tty)

docker compose run --rm -it \
  -v "$HOME/.m2:/root/.m2" \
  -v "$HOME/.gnupg:/root/.gnupg" \
  maven mvn clean deploy -Prelease
```

Alternativa: ejecutar el deploy **en el host** (fuera de Docker) si tienes Maven y GPG locales:

```bash
export GPG_TTY=$(tty)
mvn clean deploy -Prelease
```

### 4. Publicar en Central Portal

Con `autoPublish=false` (config actual), tras el upload:

1. Abre [central.sonatype.com](https://central.sonatype.com/) → tu deployment.
2. Revisa la validación.
3. Pulsa **Publish**.

En unos minutos debería aparecer en [search.maven.org](https://search.maven.org/).

### 5. Tag en Git

```bash
git tag -a v0.1.0 -m "Release 0.1.0"
git push origin v0.1.0
```

Actualiza `<scm><tag>v0.1.0</tag></scm>` en el `pom.xml` en el commit del release.

### 6. Subir versión de desarrollo

```xml
<version>0.2.0-SNAPSHOT</version>
```

## Dependencia para consumidores (tras publicar)

**Maven**

```xml
<dependency>
    <groupId>io.github.benjaminor-dev</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

**Gradle**

```kotlin
implementation("io.github.benjaminor-dev:spring-validation-plus-spring-boot-starter:0.1.0")
```

Sin repositorios extra.

## Problemas frecuentes

| Problema | Solución |
|----------|----------|
| `401 Unauthorized` en deploy | Revisa token en `~/.m2/settings.xml`, server id = `central` |
| Falla firma GPG | Verifica `gpg.keyname`, monta `~/.gnupg`, o `export GPG_TTY=$(tty)` |
| Namespace no permitido | Confirma `groupId` = `io.github.benjaminor-dev` y namespace **Verified** en Central Portal |
| Errores de Javadoc | `failOnError=false` en el perfil release; mejora docs después |
| Se subió el example | Confirma `maven.deploy.skip=true` en el pom del example |

## Pendiente

- [ ] GitHub Actions para releases
- [ ] `autoPublish=true` cuando el primer release manual funcione
