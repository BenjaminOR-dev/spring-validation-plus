#!/bin/sh
set -e

if [ ! -f .env ]; then
  echo "Missing .env — copy .env.example to .env and fill in your credentials." >&2
  exit 1
fi

# shellcheck disable=SC1091
. ./.env

for var in CENTRAL_USERNAME CENTRAL_PASSWORD GPG_KEY_ID GPG_PASSPHRASE; do
  eval "value=\${$var}"
  if [ -z "$value" ]; then
    echo "Missing $var in .env" >&2
    exit 1
  fi
done

if [ ! -f .local/gpg-signing-private.asc ]; then
  echo "Missing .local/gpg-signing-private.asc — generate or restore GPG keys first." >&2
  exit 1
fi

mkdir -p /root/.m2

cat > /root/.m2/settings.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd">

    <servers>
        <server>
            <id>central</id>
            <username>${CENTRAL_USERNAME}</username>
            <password>${CENTRAL_PASSWORD}</password>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>release</id>
            <properties>
                <gpg.keyname>${GPG_KEY_ID}</gpg.keyname>
                <gpg.passphrase>${GPG_PASSPHRASE}</gpg.passphrase>
            </properties>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>release</activeProfile>
    </activeProfiles>

</settings>
EOF

chmod 600 /root/.m2/settings.xml

printf '%s' "$GPG_PASSPHRASE" | gpg --batch --yes --pinentry-mode loopback \
  --passphrase-fd 0 \
  --import .local/gpg-signing-private.asc

exec mvn clean deploy -Prelease \
  -pl spring-validation-plus-core,spring-validation-plus-spring-boot-starter \
  -am "$@"
