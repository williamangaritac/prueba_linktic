# 🔍 Eureka Server - Service Discovery

Servidor de descubrimiento de servicios basado en Netflix Eureka para la arquitectura de microservicios de Linktic Store.

## 📋 Descripción

Eureka Server actúa como un registro centralizado donde todos los microservicios se registran automáticamente. Permite el descubrimiento dinámico de servicios sin necesidad de configuración manual de endpoints.

## 🚀 Tecnologías

- **Spring Boot**: 3.5.7
- **Spring Cloud**: 2024.0.0
- **Java**: 17
- **Netflix Eureka Server**: Service Discovery

## 🔌 Configuración

### Puerto
- **8761** - Puerto por defecto de Eureka Server

### Endpoints Principales

| Endpoint | Descripción |
|----------|-------------|
| `http://localhost:8761` | Dashboard web de Eureka |
| `http://localhost:8761/eureka/apps` | Lista de aplicaciones registradas (XML) |
| `http://localhost:8761/actuator/health` | Health check |
| `http://localhost:8761/actuator/info` | Información del servicio |

## 📦 Dependencias

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## ⚙️ Configuración (application.yml)

```yaml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false  # No se registra a sí mismo
    fetch-registry: false         # No obtiene registro de otros
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 10000
```

## 🏃 Ejecución

### Desarrollo Local

```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/eureka-server-0.0.1-SNAPSHOT.jar
```

### Con Maven Wrapper

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Con Docker

```bash
# Construir imagen
docker build -t linktic/eureka-server:latest .

# Ejecutar contenedor
docker run -p 8761:8761 linktic/eureka-server:latest
```

## 🔍 Verificación

### 1. Dashboard Web
Abrir en el navegador: http://localhost:8761

Deberías ver el dashboard de Eureka con la lista de servicios registrados.

### 2. Health Check
```bash
curl http://localhost:8761/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP"
}
```

### 3. Servicios Registrados
```bash
curl http://localhost:8761/eureka/apps
```

## 📊 Servicios que se Registran

Los siguientes microservicios se registran automáticamente en Eureka:

1. **API-GATEWAY** (puerto 8080)
2. **PRODUCTS-SERVICE** (puerto 8081)
3. **INVENTORY-SERVICE** (puerto 8082)
4. **ORDERS-SERVICE** (puerto 8083)
5. **NOTIFICATIONS-SERVICE** (puerto 8084)

## 🔧 Configuración de Clientes

Los microservicios clientes deben incluir esta configuración:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

## 📝 Notas Importantes

- **Self-Preservation**: Deshabilitado para desarrollo (evita mantener servicios caídos)
- **Eviction Interval**: 10 segundos (limpia servicios no disponibles rápidamente)
- **No se registra a sí mismo**: `register-with-eureka: false`
- **Primer servicio en iniciar**: Debe estar corriendo antes que los demás microservicios

## 🐛 Troubleshooting

### Error: Puerto 8761 en uso
```bash
# Windows
netstat -ano | findstr :8761
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8761
kill -9 <PID>
```

### Los servicios no se registran
1. Verificar que Eureka Server esté corriendo
2. Revisar la configuración `defaultZone` en los clientes
3. Verificar conectividad de red
4. Revisar logs de los servicios clientes

### Dashboard vacío
- Esperar 30-60 segundos después de iniciar los servicios
- Los servicios envían heartbeat cada 10 segundos
- Verificar logs de los microservicios

## 📚 Referencias

- [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)
- [Eureka Wiki](https://github.com/Netflix/eureka/wiki)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)

