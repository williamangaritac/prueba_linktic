# Dockerfile para Eureka Server
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar el JAR compilado
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8761

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

