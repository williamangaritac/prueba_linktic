# Dockerfile para Inventory Service - Multi-stage build
# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY src ./src

# Instalar Maven y compilar
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalar curl para healthcheck
RUN apk add --no-cache curl

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/inventory_service-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8082

# Comando para ejecutar la aplicación con perfil docker
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]

