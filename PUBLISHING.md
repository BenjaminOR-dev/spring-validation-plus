# Publishing to Maven Central

[🇪🇸 Versión en español](PUBLISHING.es.md)

This guide covers **manual** publication of `spring-validation-plus-core` and `spring-validation-plus-spring-boot-starter` to [Maven Central](https://central.sonatype.com/).

> **Not included yet:** GitHub Actions workflow (planned).

## What gets published

| Artifact | Published |
|----------|-----------|
| `spring-validation-plus-core` | Yes |
| `spring-validation-plus-spring-boot-starter` | Yes |
| `spring-validation-plus` (parent POM) | Yes |
| `spring-validation-plus-example` | **No** (`maven.deploy.skip=true`) |

## Prerequisites (one-time)

### 1. GitHub repository

Public repo: `https://github.com/BenjaminOR-dev/spring-validation-plus`

Verified Maven namespace: **`io.github.benjaminor-dev`** (Central Portal → Namespace tab).

### 2. Central Portal namespace

Already verified via GitHub as **`io.github.benjaminor-dev`**. The project `groupId` must match this namespace — do not register a separate `dev.benjaminor` unless you own that domain and verify it separately.

### 3. GPG key

```bash
gpg --full-generate-key
gpg --list-secret-keys --keyid-format long
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

### 4. Local credentials (Docker deploy)

Copy [.env.example](.env.example) to `.env` (gitignored) and fill in:

| Variable | Source |
|----------|--------|
| `CENTRAL_USERNAME` / `CENTRAL_PASSWORD` | [Central Portal → User Token](https://central.sonatype.com/usertoken) |
| `GPG_KEY_ID` | `gpg --list-secret-keys --keyid-format long` |
| `GPG_PASSPHRASE` | Your GPG key passphrase |

GPG private key backup: `.local/gpg-signing-private.asc` (see [PUBLISHING.local.md](PUBLISHING.local.md) if present locally).

For non-Docker workflows, you can still use [docs/settings-central.xml.example](docs/settings-central.xml.example) in `~/.m2/settings.xml`.

> **Security — never commit:**
>
> - `.env` (real credentials)
> - `.local/` (GPG key material)
> - Central Portal tokens or GPG passphrases in the repository

## Release checklist

### 1. Set release version (no SNAPSHOT)

In the root `pom.xml` and all modules:

```xml
<version>0.1.0</version>
```

Maven Central does not accept `-SNAPSHOT` for release repositories.

### 2. Run tests

```bash
docker compose run --rm maven mvn clean verify
```

### 3. Deploy with the `release` profile

Ensure `.env` exists (from `.env.example`) and GPG key is in `.local/`.

```bash
docker compose run --rm maven ./docker/deploy-release.sh
```

The script reads credentials from `.env`, generates `/root/.m2/settings.xml` inside the container, imports the signing key from `.local/`, and runs `mvn deploy -Prelease`.

Notes:

- Do **not** mount macOS `~/.gnupg` — `gpg-agent` fails in Docker.
- The `release` profile uses GPG loopback mode (no agent).
- Maven dependency cache uses the project `.m2/` volume from `docker-compose.yml`.

Alternatively, run deploy **on the host** (outside Docker) if you have Maven and GPG installed locally:

```bash
export GPG_TTY=$(tty)
mvn clean deploy -Prelease
```

### 4. Publish in Central Portal

With `autoPublish=false` (current default), after upload:

1. Open [central.sonatype.com](https://central.sonatype.com/) → your deployment.
2. Review validation results.
3. Click **Publish**.

Wait a few minutes until the artifact appears on [search.maven.org](https://search.maven.org/).

### 5. Git tag

```bash
git tag -a v0.1.0 -m "Release 0.1.0"
git push origin v0.1.0
```

Update `<scm><tag>v0.1.0</tag></scm>` in `pom.xml` for the next release commit.

### 6. Bump to next development version

```xml
<version>0.2.0-SNAPSHOT</version>
```

## Consumer dependency (after publish)

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

No extra repository configuration required.

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `401 Unauthorized` on deploy | Check Central Portal token in `~/.m2/settings.xml`, server id must be `central` |
| GPG signing fails | Verify `gpg.keyname`, mount `~/.gnupg`, or run `export GPG_TTY=$(tty)` |
| Namespace not allowed | Confirm `groupId` is `io.github.benjaminor-dev` and namespace shows **Verified** in Central Portal |
| Javadoc errors | `failOnError=false` is set in the release profile; fix docs over time |
| Example module uploaded | Confirm `maven.deploy.skip=true` in example `pom.xml` |

## Pending

- [ ] GitHub Actions release workflow
- [ ] `autoPublish=true` once the first manual release succeeds
