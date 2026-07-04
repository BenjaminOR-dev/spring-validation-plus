FROM eclipse-temurin:25-jdk-jammy

RUN apt-get update && apt-get install -y \
    gnupg \
    maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY docker/entrypoint.sh ./docker/entrypoint.sh
RUN chmod +x ./docker/entrypoint.sh

ENTRYPOINT ["./docker/entrypoint.sh"]
CMD ["mvn", "clean", "verify"]
