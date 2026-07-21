#!/bin/sh
set -e

ROOT_DIR="$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)"
DEMO_M2="${DEMO_M2:-$ROOT_DIR/../demo-springboot/.m2}"

mkdir -p "$DEMO_M2"

echo "Instalando spring-validation-plus en el repositorio Maven local del demo..."
echo "  origen: $ROOT_DIR"
echo "  destino: $DEMO_M2"

cd "$ROOT_DIR"
docker compose run --rm \
  -v "$DEMO_M2:/root/.m2" \
  maven mvn clean install -DskipTests -Dgpg.skip=true

echo ""
echo "Listo. El demo puede usar io.github.benjaminor-dev:spring-validation-plus-spring-boot-starter:0.3.1"
echo "Reinicia el contenedor del demo si ya estaba corriendo: cd ../demo-springboot && docker compose restart api"
