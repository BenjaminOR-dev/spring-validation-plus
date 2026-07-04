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

Public repo: `https://github.com/benjaminor/spring-validation-plus`

### 2. Central Portal namespace

1. Sign in at [central.sonatype.com](https://central.sonatype.com/) with GitHub.
2. **Register namespace** → `dev.benjaminor`.
3. Verify ownership (link the GitHub repository).
4. Wait for approval.

### 3. GPG key

```bash
gpg --full-generate-key
gpg --list-secret-keys --keyid-format long
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

### 4. Maven settings

Copy [docs/settings-central.xml.example](docs/settings-central.xml.example) into `~/.m2/settings.xml` (merge with existing settings):

- **Server `central`:** username + password from Central Portal → **Generate User Token**
- **Profile `release`:** property `gpg.keyname` = your GPG key id

> **Security — never commit to this repository:**
>
> - Central Portal tokens or passwords (keep them only in `~/.m2/settings.xml` on your machine)
> - GPG private keys or the contents of `~/.gnupg/`
> - A copy of `settings.xml` filled in with real credentials
>
> The file [docs/settings-central.xml.example](docs/settings-central.xml.example) is a **template with placeholders** — safe to keep in the public repo.

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

```bash
docker compose run --rm \
  -v "$HOME/.m2:/root/.m2" \
  -v "$HOME/.gnupg:/root/.gnupg" \
  maven mvn clean deploy -Prelease
```

Notes:

- Mount `.m2` so Central credentials and GPG settings are available.
- Mount `.gnupg` if GPG agent/keys live on the host (macOS/Linux).
- The `release` profile attaches **sources**, **javadoc**, **GPG signatures**, and uploads via `central-publishing-maven-plugin`.

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
    <groupId>dev.benjaminor</groupId>
    <artifactId>spring-validation-plus-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

**Gradle**

```kotlin
implementation("dev.benjaminor:spring-validation-plus-spring-boot-starter:0.1.0")
```

No extra repository configuration required.

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `401 Unauthorized` on deploy | Check Central Portal token in `~/.m2/settings.xml`, server id must be `central` |
| GPG signing fails | Verify `gpg.keyname`, mount `~/.gnupg`, or run `export GPG_TTY=$(tty)` |
| Namespace not allowed | Complete `dev.benjaminor` verification on Central Portal |
| Javadoc errors | `failOnError=false` is set in the release profile; fix docs over time |
| Example module uploaded | Confirm `maven.deploy.skip=true` in example `pom.xml` |

## Pending

- [ ] GitHub Actions release workflow
- [ ] `autoPublish=true` once the first manual release succeeds
