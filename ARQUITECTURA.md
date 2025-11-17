# 🏗️ ARQUITECTURA DEL SISTEMA - LINKTIC E-COMMERCE

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Arquitectura de Alto Nivel](#arquitectura-de-alto-nivel)
3. [Arquitectura de Microservicios](#arquitectura-de-microservicios)
4. [Arquitectura del Frontend](#arquitectura-del-frontend)
5. [Modelo de Datos](#modelo-de-datos)
6. [Flujos de Negocio](#flujos-de-negocio)
7. [Patrones de Diseño](#patrones-de-diseño)
8. [Seguridad y Resiliencia](#seguridad-y-resiliencia)
9. [Despliegue y DevOps](#despliegue-y-devops)

---

## 1. Visión General

### 1.1 Descripción del Sistema

Sistema de comercio electrónico empresarial construido con arquitectura de microservicios, diseñado para alta escalabilidad, mantenibilidad y resiliencia.

### 1.2 Principios Arquitectónicos

- **Separation of Concerns**: Cada microservicio tiene una responsabilidad única y bien definida
- **Domain-Driven Design (DDD)**: Modelado basado en el dominio del negocio
- **Clean Architecture**: Independencia de frameworks, UI y bases de datos
- **SOLID Principles**: Aplicados en todos los niveles del código
- **Event-Driven Architecture**: Comunicación asíncrona mediante eventos
- **API-First Design**: Contratos de API bien definidos
- **Cloud-Native**: Diseñado para entornos containerizados y cloud

### 1.3 Stack Tecnológico

#### Backend
- **Java 17** - LTS version con características modernas
- **Spring Boot 3.5.7** - Framework principal
- **Spring Cloud** - Microservicios y service discovery
- **Spring Data JPA** - Persistencia de datos
- **Apache Kafka** - Event streaming
- **PostgreSQL** - Base de datos relacional (Products, Inventory, Notifications)
- **MySQL** - Base de datos relacional (Orders)
- **Maven** - Gestión de dependencias

#### Frontend
- **Angular 18** - Framework SPA
- **TypeScript** - Lenguaje tipado
- **RxJS** - Programación reactiva
- **Tailwind CSS** - Estilos utility-first
- **Nginx** - Servidor web y reverse proxy

#### Infraestructura
- **Docker** - Containerización
- **Docker Compose** - Orquestación local
- **Eureka** - Service Discovery
- **Spring Cloud Gateway** - API Gateway
- **Zookeeper** - Coordinación distribuida

---

## 2. Arquitectura de Alto Nivel

### 2.1 Diagrama de Contexto (C4 Model - Level 1)

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│                    LINKTIC E-COMMERCE SYSTEM                    │
│                                                                 │
│  ┌──────────────┐         ┌──────────────┐                     │
│  │              │         │              │                     │
│  │   Customer   │────────▶│   Frontend   │                     │
│  │              │         │   (Angular)  │                     │
│  └──────────────┘         └──────┬───────┘                     │
│                                  │                             │
│                                  ▼                             │
│                          ┌───────────────┐                     │
│                          │               │                     │
│                          │  API Gateway  │                     │
│                          │               │                     │
│                          └───────┬───────┘                     │
│                                  │                             │
│              ┌───────────────────┼───────────────────┐         │
│              │                   │                   │         │
│              ▼                   ▼                   ▼         │
│      ┌──────────────┐    ┌──────────────┐   ┌──────────────┐  │
│      │   Products   │    │  Inventory   │   │    Orders    │  │
│      │   Service    │    │   Service    │   │   Service    │  │
│      └──────┬───────┘    └──────┬───────┘   └──────┬───────┘  │
│             │                   │                   │          │
│             ▼                   ▼                   ▼          │
│      ┌──────────────┐    ┌──────────────┐   ┌──────────────┐  │
│      │  PostgreSQL  │    │  PostgreSQL  │   │    MySQL     │  │
│      └──────────────┘    └──────────────┘   └──────┬───────┘  │
│                                                     │          │
│                                                     ▼          │
│                                             ┌──────────────┐   │
│                                             │    Kafka     │   │
│                                             └──────┬───────┘   │
│                                                     │          │
│                                                     ▼          │
│                                             ┌──────────────┐   │
│                                             │Notifications │   │
│                                             │   Service    │   │
│                                             └──────┬───────┘   │
│                                                     │          │
│                                                     ▼          │
│                                             ┌──────────────┐   │
│                                             │  PostgreSQL  │   │
│                                             └──────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Componentes Principales

| Componente | Responsabilidad | Puerto | Tecnología |
|------------|----------------|--------|------------|
| **Frontend** | Interfaz de usuario | 4200 | Angular 18 + Nginx |
| **API Gateway** | Enrutamiento, balanceo, seguridad | 8085 | Spring Cloud Gateway |
| **Eureka Server** | Service Discovery | 8761 | Spring Cloud Netflix |
| **Products Service** | Gestión de productos | 8081 | Spring Boot 3.5.7 |
| **Inventory Service** | Gestión de inventario | 8082 | Spring Boot 3.5.7 |
| **Orders Service** | Gestión de pedidos | 8083 | Spring Boot 3.5.7 |
| **Notifications Service** | Envío de notificaciones | 8084 | Spring Boot 3.5.7 |
| **Kafka** | Message Broker | 9092 | Apache Kafka 3.x |
| **PostgreSQL** | Base de datos | 5432 | PostgreSQL 15 |
| **MySQL** | Base de datos | 3306 | MySQL 8 |

---

## 3. Arquitectura de Microservicios

### 3.1 Diagrama de Contenedores (C4 Model - Level 2)

```mermaid
graph TB
    subgraph "Client Layer"
        Browser[Web Browser]
    end

    subgraph "Presentation Layer"
        Frontend[Angular Frontend<br/>Port: 4200<br/>Nginx]
    end

    subgraph "Gateway Layer"
        Gateway[API Gateway<br/>Port: 8085<br/>Spring Cloud Gateway]
        Eureka[Eureka Server<br/>Port: 8761<br/>Service Discovery]
    end

    subgraph "Business Layer - Microservices"
        Products[Products Service<br/>Port: 8081<br/>Product Management]
        Inventory[Inventory Service<br/>Port: 8082<br/>Stock Management]
        Orders[Orders Service<br/>Port: 8083<br/>Order Processing]
        Notifications[Notifications Service<br/>Port: 8084<br/>Email Notifications]
    end

    subgraph "Data Layer"
        ProductsDB[(PostgreSQL<br/>Products DB)]
        InventoryDB[(PostgreSQL<br/>Inventory DB)]
        OrdersDB[(MySQL<br/>Orders DB)]
        NotificationsDB[(PostgreSQL<br/>Notifications DB)]
    end

    subgraph "Messaging Layer"
        Kafka[Apache Kafka<br/>Event Streaming]
        Zookeeper[Zookeeper<br/>Coordination]
    end

    Browser -->|HTTP/HTTPS| Frontend
    Frontend -->|REST API| Gateway
    Gateway -->|Service Discovery| Eureka

    Gateway -->|Route /products| Products
    Gateway -->|Route /inventory| Inventory
    Gateway -->|Route /orders| Orders

    Products -->|Register| Eureka
    Inventory -->|Register| Eureka
    Orders -->|Register| Eureka
    Notifications -->|Register| Eureka

    Products -->|JPA| ProductsDB
    Inventory -->|JPA| InventoryDB
    Orders -->|JPA| OrdersDB
    Notifications -->|JPA| NotificationsDB

    Orders -->|Publish Events| Kafka
    Kafka -->|Subscribe Events| Notifications
    Kafka -->|Coordination| Zookeeper

    style Browser fill:#e1f5ff
    style Frontend fill:#bbdefb
    style Gateway fill:#90caf9
    style Eureka fill:#64b5f6
    style Products fill:#81c784
    style Inventory fill:#81c784
    style Orders fill:#81c784
    style Notifications fill:#81c784
    style ProductsDB fill:#ffb74d
    style InventoryDB fill:#ffb74d
    style OrdersDB fill:#ffb74d
    style NotificationsDB fill:#ffb74d
    style Kafka fill:#ba68c8
    style Zookeeper fill:#9c27b0
```

### 3.2 Patrones de Microservicios Implementados

#### 3.2.1 Service Discovery Pattern
- **Implementación**: Netflix Eureka
- **Propósito**: Registro y descubrimiento dinámico de servicios
- **Beneficios**:
  - Desacoplamiento de ubicaciones de servicios
  - Balanceo de carga automático
  - Health checking integrado

#### 3.2.2 API Gateway Pattern
- **Implementación**: Spring Cloud Gateway
- **Propósito**: Punto de entrada único para todos los clientes
- **Responsabilidades**:
  - Enrutamiento de peticiones
  - Balanceo de carga
  - Circuit Breaker (Resilience4j)
  - Rate Limiting
  - CORS handling
  - Request/Response transformation

#### 3.2.3 Database per Service Pattern
- **Implementación**: Cada microservicio tiene su propia base de datos
- **Propósito**: Independencia de datos y desacoplamiento
- **Bases de Datos**:
  - Products Service → PostgreSQL (products_db)
  - Inventory Service → PostgreSQL (inventory_db)
  - Orders Service → MySQL (orders_db)
  - Notifications Service → PostgreSQL (notifications_db)

#### 3.2.4 Event-Driven Architecture Pattern
- **Implementación**: Apache Kafka
- **Propósito**: Comunicación asíncrona entre servicios
- **Flujo**:
  1. Orders Service publica evento `OrderCreatedEvent`
  2. Kafka almacena el evento en topic `order-events`
  3. Notifications Service consume el evento
  4. Notifications Service envía email al cliente

#### 3.2.5 Circuit Breaker Pattern
- **Implementación**: Resilience4j
- **Propósito**: Prevenir cascadas de fallos
- **Configuración**:
  ```yaml
  resilience4j:
    circuitbreaker:
      instances:
        productsCircuitBreaker:
          slidingWindowSize: 10
          failureRateThreshold: 50
          waitDurationInOpenState: 10000
  ```

### 3.3 Arquitectura de Cada Microservicio (Hexagonal Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                    MICROSERVICE STRUCTURE                   │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              PRESENTATION LAYER (Controllers)         │  │
│  │  - REST Controllers                                   │  │
│  │  - Request/Response DTOs                              │  │
│  │  - Validation (@Valid)                                │  │
│  │  - Exception Handlers (@ControllerAdvice)             │  │
│  └─────────────────────┬─────────────────────────────────┘  │
│                        │                                    │
│                        ▼                                    │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              APPLICATION LAYER (Services)             │  │
│  │  - Business Logic                                     │  │
│  │  - Transaction Management (@Transactional)            │  │
│  │  - Service Interfaces                                 │  │
│  │  - Use Cases Implementation                           │  │
│  └─────────────────────┬─────────────────────────────────┘  │
│                        │                                    │
│                        ▼                                    │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              DOMAIN LAYER (Entities)                  │  │
│  │  - Domain Models (@Entity)                            │  │
│  │  - Business Rules                                     │  │
│  │  - Domain Events                                      │  │
│  │  - Value Objects                                      │  │
│  └─────────────────────┬─────────────────────────────────┘  │
│                        │                                    │
│                        ▼                                    │
│  ┌───────────────────────────────────────────────────────┐  │
│  │           INFRASTRUCTURE LAYER (Repositories)         │  │
│  │  - JPA Repositories                                   │  │
│  │  - Database Configuration                             │  │
│  │  - External Service Clients                           │  │
│  │  - Kafka Producers/Consumers                          │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. Arquitectura del Frontend

### 4.1 Clean Architecture en Angular

```
frontend_angular/
├── src/
│   ├── app/
│   │   ├── core/                          # DOMAIN LAYER
│   │   │   ├── models/                    # Entidades del dominio
│   │   │   │   ├── product.model.ts       # Product, ProductRequest
│   │   │   │   ├── inventory.model.ts     # Inventory
│   │   │   │   ├── order.model.ts         # Order, OrderRequest
│   │   │   │   └── page.model.ts          # Page<T> (Paginación)
│   │   │   │
│   │   │   ├── interfaces/                # Contratos (Ports)
│   │   │   │   ├── product-repository.interface.ts
│   │   │   │   ├── inventory-repository.interface.ts
│   │   │   │   └── order-repository.interface.ts
│   │   │   │
│   │   │   └── use-cases/                 # Casos de uso (Business Logic)
│   │   │       ├── get-products.use-case.ts
│   │   │       ├── create-product.use-case.ts
│   │   │       ├── get-inventory.use-case.ts
│   │   │       └── create-order.use-case.ts
│   │   │
│   │   ├── infrastructure/                # INFRASTRUCTURE LAYER
│   │   │   └── repositories/              # Implementaciones (Adapters)
│   │   │       ├── product.repository.ts  # HTTP calls to backend
│   │   │       ├── inventory.repository.ts
│   │   │       └── order.repository.ts
│   │   │
│   │   ├── presentation/                  # PRESENTATION LAYER
│   │   │   ├── components/                # Componentes reutilizables
│   │   │   │   ├── product-card/
│   │   │   │   ├── create-product-modal/
│   │   │   │   └── order-form/
│   │   │   │
│   │   │   └── pages/                     # Páginas (Smart Components)
│   │   │       ├── catalog/               # Catálogo de productos
│   │   │       ├── product-detail/        # Detalle de producto
│   │   │       └── checkout/              # Proceso de compra
│   │   │
│   │   ├── config/                        # Configuración
│   │   │   └── app.config.ts              # Dependency Injection
│   │   │
│   │   └── app.component.ts               # Root component
│   │
│   ├── environments/                      # Variables de entorno
│   │   ├── environment.ts                 # Development
│   │   └── environment.prod.ts            # Production
│   │
│   └── assets/                            # Recursos estáticos
│
├── nginx.conf                             # Configuración Nginx
├── Dockerfile                             # Multi-stage build
└── package.json                           # Dependencias
```

### 4.2 Principios de Clean Architecture Aplicados

#### 4.2.1 Dependency Rule
```
Presentation → Use Cases → Entities
     ↓              ↓
Infrastructure ←────┘
```

**Regla**: Las dependencias apuntan hacia adentro. Las capas internas no conocen las externas.

#### 4.2.2 Dependency Injection

```typescript
// app.config.ts
export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    {
      provide: PRODUCT_REPOSITORY_TOKEN,
      useClass: ProductRepository  // Inyección de implementación
    }
  ]
};

// create-product.use-case.ts
@Injectable({ providedIn: 'root' })
export class CreateProductUseCase {
  constructor(
    @Inject(PRODUCT_REPOSITORY_TOKEN)
    private productRepository: IProductRepository  // Depende de interfaz
  ) {}

  execute(request: ProductRequest): Observable<Product> {
    return this.productRepository.createProduct(request);
  }
}
```

#### 4.2.3 Separation of Concerns

| Capa | Responsabilidad | Ejemplo |
|------|----------------|---------|
| **Presentation** | UI, eventos de usuario, presentación de datos | `CatalogComponent`, `CreateProductModalComponent` |
| **Use Cases** | Lógica de negocio, orquestación | `CreateProductUseCase`, `GetProductsUseCase` |
| **Domain** | Modelos, reglas de negocio | `Product`, `ProductRequest`, `IProductRepository` |
| **Infrastructure** | Comunicación externa, HTTP, persistencia | `ProductRepository` (HTTP calls) |

### 4.3 Flujo de Datos en el Frontend

```mermaid
sequenceDiagram
    participant User
    participant Component as Presentation<br/>(Component)
    participant UseCase as Application<br/>(Use Case)
    participant Repository as Infrastructure<br/>(Repository)
    participant Backend as Backend API

    User->>Component: Click "Crear Producto"
    Component->>Component: Show Modal
    User->>Component: Fill Form & Submit
    Component->>Component: Validate Form
    Component->>UseCase: execute(productRequest)
    UseCase->>Repository: createProduct(productRequest)
    Repository->>Backend: POST /api/v1/products
    Backend-->>Repository: 201 Created + Product
    Repository-->>UseCase: Observable<Product>
    UseCase-->>Component: Observable<Product>
    Component->>Component: Update UI
    Component->>User: Show Success Message
```

### 4.4 Configuración de Nginx como Reverse Proxy

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Servir archivos estáticos de Angular
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Proxy para el API Gateway
    location /api/ {
        proxy_pass http://api-gateway:8080/api/;
        proxy_http_version 1.1;

        # Headers necesarios
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header Content-Type $content_type;
        proxy_set_header Accept $http_accept;

        # CORS
        add_header 'Access-Control-Allow-Origin' '*' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS, PATCH' always;
    }
}
```

**Ventajas**:
- ✅ Evita problemas de CORS
- ✅ Simplifica configuración del frontend (URLs relativas)
- ✅ Permite cambiar backend sin modificar frontend
- ✅ Cacheo de respuestas
- ✅ Compresión gzip

---

## 5. Modelo de Datos

### 5.1 Diagrama Entidad-Relación

```mermaid
erDiagram
    PRODUCT {
        bigint id PK
        varchar sku UK "Unique identifier"
        varchar name
        text description
        decimal price
        boolean status
        timestamp created_at
        timestamp updated_at
    }

    INVENTORY {
        bigint id PK
        varchar product_sku FK "References Product"
        int quantity
        int reserved_quantity
        timestamp last_updated
    }

    ORDER {
        bigint id PK
        varchar order_number UK
        varchar customer_name
        varchar customer_email
        varchar customer_address
        decimal total_amount
        varchar status "PENDING, CONFIRMED, CANCELLED"
        timestamp created_at
        timestamp updated_at
    }

    ORDER_ITEM {
        bigint id PK
        bigint order_id FK
        varchar product_sku
        varchar product_name
        int quantity
        decimal unit_price
        decimal subtotal
    }

    NOTIFICATION {
        bigint id PK
        varchar recipient_email
        varchar subject
        text message
        varchar status "PENDING, SENT, FAILED"
        timestamp sent_at
        timestamp created_at
    }

    PRODUCT ||--o| INVENTORY : "has"
    ORDER ||--|{ ORDER_ITEM : "contains"
    ORDER ||--o{ NOTIFICATION : "triggers"
```

### 5.2 Esquemas de Base de Datos

#### 5.2.1 Products Database (PostgreSQL)

```sql
-- products_db.products
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    status BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_created_at ON products(created_at DESC);
```

**Entidad JPA**:
```java
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

#### 5.2.2 Inventory Database (PostgreSQL)

```sql
-- inventory_db.inventory
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_sku VARCHAR(255) NOT NULL UNIQUE,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    reserved_quantity INTEGER NOT NULL DEFAULT 0 CHECK (reserved_quantity >= 0),
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_available_quantity CHECK (quantity >= reserved_quantity)
);

CREATE INDEX idx_inventory_product_sku ON inventory(product_sku);
```

#### 5.2.3 Orders Database (MySQL)

```sql
-- orders_db.orders
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_address TEXT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_number (order_number),
    INDEX idx_customer_email (customer_email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- orders_db.order_items
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_sku VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_product_sku (product_sku)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 5.2.4 Notifications Database (PostgreSQL)

```sql
-- notifications_db.notifications
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_email);
```

### 5.3 Estrategias de Persistencia

#### 5.3.1 Transacciones
```java
@Service
@Transactional
public class ProductService {

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        // Read-only optimization
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductResponse createProduct(ProductRequest request) {
        // Ensures SKU uniqueness
    }
}
```

#### 5.3.2 Optimistic Locking
```java
@Entity
public class Inventory {
    @Version
    private Long version;  // Prevents lost updates
}
```

#### 5.3.3 Paginación
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByStatusTrue(Pageable pageable);
}
```

---

## 6. Flujos de Negocio

### 6.1 Flujo: Ver Detalle de Producto

#### 6.1.1 Diagrama de Secuencia

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant Nginx
    participant Gateway as API Gateway
    participant Eureka
    participant Products as Products Service
    participant DB as PostgreSQL

    User->>Browser: Click en producto
    Browser->>Browser: Navigate to /product/:sku

    Note over Browser: ProductDetailComponent
    Browser->>Nginx: GET /api/v1/products/{sku}
    Nginx->>Gateway: Proxy to http://api-gateway:8080

    Gateway->>Eureka: Resolve PRODUCTS-SERVICE
    Eureka-->>Gateway: Return instance(s)

    Gateway->>Products: GET /api/v1/products/{sku}

    Note over Products: ProductController
    Products->>Products: ProductService.getProductBySku(sku)
    Products->>DB: SELECT * FROM products WHERE sku = ?
    DB-->>Products: Product entity

    Products->>Products: Map to ProductResponse
    Products-->>Gateway: 200 OK + ProductResponse
    Gateway-->>Nginx: 200 OK + ProductResponse
    Nginx-->>Browser: 200 OK + ProductResponse

    Browser->>Browser: Render product details
    Browser->>User: Display product information
```

#### 6.1.2 Código del Flujo

**Frontend - ProductDetailComponent**:
```typescript
export class ProductDetailComponent implements OnInit {
  product$!: Observable<Product>;

  constructor(
    private route: ActivatedRoute,
    private getProductUseCase: GetProductBySkuUseCase
  ) {}

  ngOnInit(): void {
    const sku = this.route.snapshot.paramMap.get('sku')!;
    this.product$ = this.getProductUseCase.execute(sku);
  }
}
```

**Backend - ProductController**:
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        ProductResponse product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }
}
```

**Backend - ProductService**:
```java
@Service
@Transactional(readOnly = true)
public class ProductService {

    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        return mapToResponse(product);
    }
}
```

#### 6.1.3 Manejo de Errores

| Código | Escenario | Respuesta |
|--------|-----------|-----------|
| **200** | Producto encontrado | ProductResponse |
| **404** | SKU no existe | `{"error": "Product not found with SKU: XXX"}` |
| **500** | Error de base de datos | `{"error": "Internal server error"}` |
| **503** | Servicio no disponible | Circuit Breaker fallback |

---

### 6.2 Flujo: Crear Nuevo Producto

#### 6.2.1 Diagrama de Secuencia

```mermaid
sequenceDiagram
    actor User
    participant Modal as CreateProductModal
    participant UseCase as CreateProductUseCase
    participant Repo as ProductRepository
    participant Nginx
    participant Gateway as API Gateway
    participant Products as Products Service
    participant DB as PostgreSQL

    User->>Modal: Click "Crear Producto"
    Modal->>Modal: Show modal form
    User->>Modal: Fill form (SKU, Name, Price, etc.)
    User->>Modal: Click "Crear"

    Modal->>Modal: Validate form
    alt Form invalid
        Modal->>User: Show validation errors
    else Form valid
        Modal->>UseCase: execute(productRequest)
        UseCase->>Repo: createProduct(productRequest)
        Repo->>Nginx: POST /api/v1/products + JSON body
        Nginx->>Gateway: Proxy request
        Gateway->>Products: POST /api/v1/products

        Products->>Products: Validate @Valid ProductRequest

        alt Validation fails
            Products-->>Gateway: 400 Bad Request
            Gateway-->>Nginx: 400 Bad Request
            Nginx-->>Repo: 400 Bad Request
            Repo-->>UseCase: Error Observable
            UseCase-->>Modal: Error Observable
            Modal->>User: "Datos inválidos"
        else Validation OK
            Products->>DB: Check if SKU exists

            alt SKU already exists
                DB-->>Products: Product found
                Products-->>Gateway: 409 Conflict
                Gateway-->>Nginx: 409 Conflict
                Nginx-->>Repo: 409 Conflict
                Repo-->>UseCase: Error Observable
                UseCase-->>Modal: Error Observable
                Modal->>User: "SKU ya existe"
            else SKU unique
                Products->>DB: INSERT INTO products
                DB-->>Products: Product created
                Products->>Products: Map to ProductResponse
                Products-->>Gateway: 201 Created + ProductResponse
                Gateway-->>Nginx: 201 Created
                Nginx-->>Repo: 201 Created
                Repo-->>UseCase: Observable<Product>
                UseCase-->>Modal: Observable<Product>
                Modal->>Modal: Close modal
                Modal->>Modal: Emit productCreated event
                Modal->>User: "Producto creado exitosamente"
            end
        end
    end
```

#### 6.2.2 Código del Flujo

**Frontend - CreateProductModalComponent**:
```typescript
export class CreateProductModalComponent {
  productForm: FormGroup;
  @Output() productCreated = new EventEmitter<void>();

  constructor(
    private fb: FormBuilder,
    private createProductUseCase: CreateProductUseCase
  ) {
    this.productForm = this.fb.group({
      sku: ['', [Validators.required, Validators.minLength(3)]],
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      price: ['', [Validators.required, Validators.min(0)]],
      status: [true]
    });
  }

  onSubmit(): void {
    if (this.productForm.invalid) return;

    const productRequest: ProductRequest = this.productForm.value;

    this.createProductUseCase.execute(productRequest).subscribe({
      next: (product) => {
        this.productCreated.emit();
        this.onClose();
      },
      error: (error) => {
        if (error.status === 409) {
          this.errorMessage = `El producto con SKU "${productRequest.sku}" ya existe`;
        } else if (error.status === 400) {
          this.errorMessage = 'Datos inválidos. Por favor verifica los campos';
        } else {
          this.errorMessage = 'Error al crear el producto. Intenta nuevamente';
        }
      }
    });
  }
}
```

**Backend - ProductController**:
```java
@PostMapping
public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody ProductRequest productRequest) {
    log.info("Received request to create product with SKU: {}", productRequest.getSku());

    try {
        ProductResponse response = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (DuplicateResourceException e) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
    }
}
```

**Backend - ProductService**:
```java
@Service
@Transactional
public class ProductService {

    public ProductResponse createProduct(ProductRequest request) {
        // Validate SKU uniqueness
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException(
                "Product with SKU " + request.getSku() + " already exists"
            );
        }

        // Create entity
        Product product = Product.builder()
            .sku(request.getSku())
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .status(request.getStatus())
            .build();

        // Save to database
        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return mapToResponse(savedProduct);
    }
}
```

**Backend - ProductRequest DTO**:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "SKU is required")
    @Size(min = 3, max = 255, message = "SKU must be between 3 and 255 characters")
    private String sku;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @NotNull(message = "Status is required")
    private Boolean status;
}
```

#### 6.2.3 Validaciones Implementadas

| Nivel | Validación | Implementación |
|-------|-----------|----------------|
| **Frontend** | Campos requeridos | `Validators.required` |
| **Frontend** | Longitud mínima | `Validators.minLength(3)` |
| **Frontend** | Precio positivo | `Validators.min(0)` |
| **Backend** | Bean Validation | `@Valid @RequestBody` |
| **Backend** | SKU único | `productRepository.existsBySku()` |
| **Database** | Constraint UNIQUE | `UNIQUE(sku)` |
| **Database** | Constraint CHECK | `CHECK (price >= 0)` |

---

### 6.3 Flujo: Comprar un Producto (Crear Orden)

#### 6.3.1 Diagrama de Secuencia Completo

```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant Gateway as API Gateway
    participant Orders as Orders Service
    participant OrdersDB as MySQL (Orders)
    participant Kafka
    participant Notifications as Notifications Service
    participant NotifDB as PostgreSQL (Notifications)
    participant Email as Email Server

    User->>Frontend: Fill checkout form
    User->>Frontend: Click "Realizar Pedido"

    Frontend->>Gateway: POST /api/v1/orders
    Gateway->>Orders: POST /api/v1/orders

    Note over Orders: OrderController
    Orders->>Orders: Validate OrderRequest
    Orders->>Orders: Generate order number
    Orders->>Orders: Calculate total amount

    Orders->>OrdersDB: BEGIN TRANSACTION
    Orders->>OrdersDB: INSERT INTO orders
    OrdersDB-->>Orders: Order created (ID)

    loop For each item
        Orders->>OrdersDB: INSERT INTO order_items
    end

    Orders->>OrdersDB: COMMIT TRANSACTION

    Note over Orders: Create OrderEvent
    Orders->>Kafka: Publish OrderCreatedEvent
    Note over Kafka: Topic: order-events
    Kafka-->>Orders: Event published

    Orders-->>Gateway: 201 Created + OrderResponse
    Gateway-->>Frontend: 201 Created + OrderResponse
    Frontend->>User: "Pedido creado exitosamente"

    Note over Kafka,Notifications: Asynchronous Processing

    Kafka->>Notifications: Consume OrderCreatedEvent
    Note over Notifications: NotificationConsumer

    Notifications->>Notifications: Create email content
    Notifications->>NotifDB: INSERT INTO notifications (PENDING)

    Notifications->>Email: Send email via SMTP

    alt Email sent successfully
        Email-->>Notifications: Success
        Notifications->>NotifDB: UPDATE status = SENT
        Notifications->>Notifications: Log success
    else Email failed
        Email-->>Notifications: Error
        Notifications->>NotifDB: UPDATE status = FAILED
        Notifications->>Notifications: Log error
    end
```

#### 6.3.2 Código del Flujo

**Frontend - CheckoutComponent**:
```typescript
export class CheckoutComponent {
  checkoutForm: FormGroup;
  cartItems: CartItem[] = [];

  constructor(
    private fb: FormBuilder,
    private createOrderUseCase: CreateOrderUseCase,
    private router: Router
  ) {
    this.checkoutForm = this.fb.group({
      customerName: ['', Validators.required],
      customerEmail: ['', [Validators.required, Validators.email]],
      customerAddress: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.checkoutForm.invalid) return;

    const orderRequest: OrderRequest = {
      customerName: this.checkoutForm.value.customerName,
      customerEmail: this.checkoutForm.value.customerEmail,
      customerAddress: this.checkoutForm.value.customerAddress,
      items: this.cartItems.map(item => ({
        productSku: item.product.sku,
        productName: item.product.name,
        quantity: item.quantity,
        unitPrice: item.product.price
      }))
    };

    this.createOrderUseCase.execute(orderRequest).subscribe({
      next: (order) => {
        alert(`Pedido ${order.orderNumber} creado exitosamente!`);
        this.router.navigate(['/orders', order.orderNumber]);
      },
      error: (error) => {
        alert('Error al crear el pedido. Intenta nuevamente.');
      }
    });
  }
}
```

**Backend - OrderController**:
```java
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest orderRequest) {
        log.info("Received request to create order for customer: {}",
                 orderRequest.getCustomerEmail());

        OrderResponse response = orderService.createOrder(orderRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

**Backend - OrderService**:
```java
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderResponse createOrder(OrderRequest request) {
        // 1. Generate unique order number
        String orderNumber = generateOrderNumber();

        // 2. Calculate total amount
        BigDecimal totalAmount = request.getItems().stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Create Order entity
        Order order = Order.builder()
            .orderNumber(orderNumber)
            .customerName(request.getCustomerName())
            .customerEmail(request.getCustomerEmail())
            .customerAddress(request.getCustomerAddress())
            .totalAmount(totalAmount)
            .status(OrderStatus.PENDING)
            .build();

        // 4. Save order
        Order savedOrder = orderRepository.save(order);

        // 5. Create and save order items
        List<OrderItem> orderItems = request.getItems().stream()
            .map(itemRequest -> OrderItem.builder()
                .order(savedOrder)
                .productSku(itemRequest.getProductSku())
                .productName(itemRequest.getProductName())
                .quantity(itemRequest.getQuantity())
                .unitPrice(itemRequest.getUnitPrice())
                .subtotal(itemRequest.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                .build())
            .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        // 6. Publish event to Kafka
        publishOrderCreatedEvent(savedOrder);

        log.info("Order created successfully: {}", orderNumber);

        return mapToResponse(savedOrder);
    }

    private void publishOrderCreatedEvent(Order order) {
        OrderEvent event = OrderEvent.builder()
            .orderNumber(order.getOrderNumber())
            .customerName(order.getCustomerName())
            .customerEmail(order.getCustomerEmail())
            .totalAmount(order.getTotalAmount())
            .items(order.getItems().stream()
                .map(item -> new OrderItemDTO(
                    item.getProductSku(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice()
                ))
                .collect(Collectors.toList()))
            .build();

        kafkaTemplate.send("order-events", event);
        log.info("Published OrderCreatedEvent for order: {}", order.getOrderNumber());
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}
```

**Backend - NotificationConsumer**:
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "order-events",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event for order: {}", orderEvent.getOrderNumber());

        try {
            notificationService.sendOrderConfirmationEmail(orderEvent);
            log.info("Notification sent successfully for order: {}",
                     orderEvent.getOrderNumber());
        } catch (Exception e) {
            log.error("Error sending notification for order: {}",
                      orderEvent.getOrderNumber(), e);
        }
    }
}
```

**Backend - NotificationService**:
```java
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(OrderEvent orderEvent) {
        // 1. Create notification record
        Notification notification = Notification.builder()
            .recipientEmail(orderEvent.getCustomerEmail())
            .subject("Confirmación de Pedido - " + orderEvent.getOrderNumber())
            .message(buildEmailMessage(orderEvent))
            .status(NotificationStatus.PENDING)
            .build();

        Notification savedNotification = notificationRepository.save(notification);

        try {
            // 2. Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(orderEvent.getCustomerEmail());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getMessage(), true);
            helper.setFrom("noreply@linktic.com");

            mailSender.send(message);

            // 3. Update notification status
            savedNotification.setStatus(NotificationStatus.SENT);
            savedNotification.setSentAt(LocalDateTime.now());
            notificationRepository.save(savedNotification);

            log.info("Email sent successfully to: {}", orderEvent.getCustomerEmail());

        } catch (Exception e) {
            // 4. Handle failure
            savedNotification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(savedNotification);

            log.error("Failed to send email to: {}", orderEvent.getCustomerEmail(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildEmailMessage(OrderEvent orderEvent) {
        StringBuilder message = new StringBuilder();
        message.append("<html><body>");
        message.append("<h2>¡Gracias por tu pedido!</h2>");
        message.append("<p>Hola ").append(orderEvent.getCustomerName()).append(",</p>");
        message.append("<p>Tu pedido <strong>").append(orderEvent.getOrderNumber())
               .append("</strong> ha sido recibido.</p>");
        message.append("<h3>Detalles del Pedido:</h3>");
        message.append("<ul>");

        for (OrderItemDTO item : orderEvent.getItems()) {
            message.append("<li>")
                   .append(item.getProductName())
                   .append(" - Cantidad: ").append(item.getQuantity())
                   .append(" - Precio: $").append(item.getUnitPrice())
                   .append("</li>");
        }

        message.append("</ul>");
        message.append("<p><strong>Total: $").append(orderEvent.getTotalAmount())
               .append("</strong></p>");
        message.append("<p>Recibirás un correo cuando tu pedido sea enviado.</p>");
        message.append("</body></html>");

        return message.toString();
    }
}
```

#### 6.3.3 Configuración de Kafka

**application.yml (Orders Service)**:
```yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.type.mapping: orderEvent:com.linktic_test.orders_service.events.OrderEvent
```

**application.yml (Notifications Service)**:
```yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092
    consumer:
      group-id: notification-service-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.type.mapping: orderEvent:com.linktic_test.notifications_service.dto.OrderEventDTO
        spring.json.trusted.packages: "*"
```

#### 6.3.4 Garantías de Entrega

| Aspecto | Implementación | Garantía |
|---------|----------------|----------|
| **Transaccionalidad** | `@Transactional` en OrderService | ACID en base de datos |
| **Idempotencia** | Order number único | No duplicados |
| **At-least-once delivery** | Kafka consumer auto-commit | Evento procesado al menos una vez |
| **Error handling** | Try-catch + status FAILED | Registro de fallos |
| **Retry mechanism** | Kafka consumer retry | Reintentos automáticos |

---

## 7. Patrones de Diseño

### 7.1 Patrones Arquitectónicos

#### 7.1.1 Microservices Pattern
**Problema**: Aplicaciones monolíticas difíciles de escalar y mantener.

**Solución**: Dividir la aplicación en servicios pequeños e independientes.

**Implementación**:
- Products Service: Gestión de productos
- Inventory Service: Gestión de inventario
- Orders Service: Gestión de pedidos
- Notifications Service: Envío de notificaciones

**Beneficios**:
- ✅ Escalabilidad independiente
- ✅ Despliegue independiente
- ✅ Tecnologías heterogéneas
- ✅ Aislamiento de fallos

#### 7.1.2 API Gateway Pattern
**Problema**: Clientes necesitan comunicarse con múltiples microservicios.

**Solución**: Punto de entrada único que enruta peticiones.

**Implementación**: Spring Cloud Gateway

**Responsabilidades**:
- Enrutamiento basado en paths
- Balanceo de carga
- Circuit Breaker
- Rate Limiting
- CORS handling
- Authentication/Authorization (futuro)

#### 7.1.3 Service Registry Pattern (Service Discovery)
**Problema**: Ubicaciones de servicios dinámicas en entornos cloud.

**Solución**: Registro centralizado de servicios.

**Implementación**: Netflix Eureka

**Flujo**:
1. Servicios se registran al iniciar
2. Envían heartbeats periódicos
3. Gateway consulta el registro
4. Gateway obtiene instancias disponibles

#### 7.1.4 Event-Driven Architecture
**Problema**: Acoplamiento entre servicios con comunicación síncrona.

**Solución**: Comunicación asíncrona mediante eventos.

**Implementación**: Apache Kafka

**Ventajas**:
- ✅ Desacoplamiento temporal
- ✅ Escalabilidad
- ✅ Resiliencia
- ✅ Event sourcing posible

#### 7.1.5 Database per Service
**Problema**: Base de datos compartida crea acoplamiento.

**Solución**: Cada servicio tiene su propia base de datos.

**Implementación**:
```
Products Service    → PostgreSQL (products_db)
Inventory Service   → PostgreSQL (inventory_db)
Orders Service      → MySQL (orders_db)
Notifications Service → PostgreSQL (notifications_db)
```

**Trade-offs**:
- ✅ Independencia de datos
- ✅ Tecnologías específicas por servicio
- ❌ Transacciones distribuidas complejas
- ❌ Joins entre servicios no posibles

#### 7.1.6 Circuit Breaker Pattern
**Problema**: Fallos en cascada cuando un servicio falla.

**Solución**: Detener llamadas a servicios que fallan.

**Implementación**: Resilience4j

**Estados**:
```
CLOSED → OPEN → HALF_OPEN → CLOSED
```

**Configuración**:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      productsCircuitBreaker:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
```

### 7.2 Patrones de Diseño (GoF)

#### 7.2.1 Repository Pattern
**Propósito**: Abstracción de la capa de persistencia.

**Implementación**:
```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    Page<Product> findByStatusTrue(Pageable pageable);
}
```

**Beneficios**:
- ✅ Desacoplamiento de la lógica de negocio
- ✅ Facilita testing (mocking)
- ✅ Centraliza queries

#### 7.2.2 Data Transfer Object (DTO) Pattern
**Propósito**: Transferir datos entre capas sin exponer entidades.

**Implementación**:
```java
// Request DTO
@Data
public class ProductRequest {
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean status;
}

// Response DTO
@Data
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Beneficios**:
- ✅ Oculta detalles de implementación
- ✅ Versionado de API
- ✅ Validación específica por operación

#### 7.2.3 Builder Pattern
**Propósito**: Construcción de objetos complejos.

**Implementación**:
```java
@Entity
@Builder
@Data
public class Order {
    private Long id;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private BigDecimal totalAmount;
    private OrderStatus status;
}

// Uso
Order order = Order.builder()
    .orderNumber("ORD-123")
    .customerName("John Doe")
    .customerEmail("john@example.com")
    .totalAmount(new BigDecimal("100.00"))
    .status(OrderStatus.PENDING)
    .build();
```

#### 7.2.4 Dependency Injection Pattern
**Propósito**: Inversión de control y desacoplamiento.

**Implementación**:
```java
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Dependencies injected via constructor
}
```

**Beneficios**:
- ✅ Testabilidad (mock dependencies)
- ✅ Flexibilidad
- ✅ Bajo acoplamiento

#### 7.2.5 Strategy Pattern
**Propósito**: Algoritmos intercambiables.

**Implementación** (Frontend):
```typescript
// Strategy interface
export interface IProductRepository {
  getProducts(page: number): Observable<Page<Product>>;
  createProduct(request: ProductRequest): Observable<Product>;
}

// Concrete strategy
@Injectable()
export class ProductRepository implements IProductRepository {
  // Implementation using HTTP
}

// Context
@Injectable()
export class GetProductsUseCase {
  constructor(
    @Inject(PRODUCT_REPOSITORY_TOKEN)
    private repository: IProductRepository
  ) {}
}
```

#### 7.2.6 Observer Pattern
**Propósito**: Notificación de cambios.

**Implementación** (Frontend - RxJS):
```typescript
export class CatalogComponent {
  products$: Observable<Page<Product>>;

  ngOnInit(): void {
    this.products$ = this.getProductsUseCase.execute(this.currentPage);
  }

  onProductCreated(): void {
    // Re-fetch products when new product is created
    this.loadProducts();
  }
}
```

**Implementación** (Backend - Kafka):
```java
// Publisher (Subject)
kafkaTemplate.send("order-events", orderEvent);

// Subscriber (Observer)
@KafkaListener(topics = "order-events")
public void consumeOrderEvent(OrderEvent event) {
    // React to event
}
```

#### 7.2.7 Template Method Pattern
**Propósito**: Definir esqueleto de algoritmo.

**Implementación**:
```java
@Entity
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

// Subclasses inherit the template
@Entity
public class Product extends BaseEntity {
    // Specific fields
}
```

### 7.3 Patrones de Integración

#### 7.3.1 Publish-Subscribe Pattern
**Implementación**: Kafka Topics

```
Orders Service → [order-events topic] → Notifications Service
                                      → Inventory Service (future)
                                      → Analytics Service (future)
```

#### 7.3.2 Request-Response Pattern
**Implementación**: REST APIs

```
Frontend → API Gateway → Products Service → Database
        ←              ←                  ←
```

#### 7.3.3 Saga Pattern (Future Enhancement)
**Propósito**: Transacciones distribuidas.

**Ejemplo**: Proceso de compra
1. Create Order (Orders Service)
2. Reserve Inventory (Inventory Service)
3. Process Payment (Payment Service)
4. Send Notification (Notifications Service)

**Compensating Transactions**:
- Si falla el pago → Liberar inventario + Cancelar orden

---

## 8. Seguridad y Resiliencia

### 8.1 Seguridad

#### 8.1.1 Medidas de Seguridad Implementadas

| Capa | Medida | Implementación |
|------|--------|----------------|
| **Network** | Aislamiento de red | Docker networks |
| **API** | CORS | Spring Security + Nginx |
| **Data** | Validación de entrada | Bean Validation (`@Valid`) |
| **Data** | SQL Injection prevention | JPA/Hibernate (Prepared Statements) |
| **Headers** | Security headers | Spring Security defaults |

#### 8.1.2 CORS Configuration

**Nginx**:
```nginx
add_header 'Access-Control-Allow-Origin' '*' always;
add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS, PATCH' always;
add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization, Accept' always;
```

**Spring Boot**:
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
```

#### 8.1.3 Input Validation

**Frontend**:
```typescript
this.productForm = this.fb.group({
  sku: ['', [Validators.required, Validators.minLength(3)]],
  name: ['', [Validators.required, Validators.minLength(3)]],
  price: ['', [Validators.required, Validators.min(0)]],
  status: [true]
});
```

**Backend**:
```java
@Data
public class ProductRequest {
    @NotBlank(message = "SKU is required")
    @Size(min = 3, max = 255)
    private String sku;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 255)
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;
}
```

#### 8.1.4 Security Headers

Spring Security agrega automáticamente:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 0`
- `Referrer-Policy: no-referrer`
- `Cache-Control: no-cache, no-store, max-age=0, must-revalidate`

#### 8.1.5 Mejoras de Seguridad Futuras

| Mejora | Descripción | Prioridad |
|--------|-------------|-----------|
| **JWT Authentication** | Autenticación basada en tokens | Alta |
| **OAuth 2.0** | Autorización delegada | Alta |
| **Rate Limiting** | Prevenir abuso de API | Media |
| **API Keys** | Identificación de clientes | Media |
| **Encryption at rest** | Cifrado de datos sensibles | Media |
| **HTTPS** | TLS/SSL en producción | Alta |
| **Secret Management** | Vault para credenciales | Alta |

### 8.2 Resiliencia

#### 8.2.1 Circuit Breaker

**Configuración**:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      productsCircuitBreaker:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
```

**Estados**:
```
CLOSED (Normal operation)
   ↓ (50% failures in 10 requests)
OPEN (Reject all requests)
   ↓ (After 10 seconds)
HALF_OPEN (Allow 3 test requests)
   ↓ (If successful)
CLOSED
```

**Fallback**:
```java
@GetMapping("/products")
public ResponseEntity<Page<ProductResponse>> getProducts() {
    try {
        return ResponseEntity.ok(productService.getAllProducts());
    } catch (Exception e) {
        // Fallback: return cached data or empty list
        return ResponseEntity.ok(Page.empty());
    }
}
```

#### 8.2.2 Retry Mechanism

**Kafka Consumer**:
```yaml
spring:
  kafka:
    consumer:
      properties:
        max.poll.interval.ms: 300000
        session.timeout.ms: 10000
    listener:
      ack-mode: manual
```

**HTTP Clients** (Future):
```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofSeconds(5))
        .setReadTimeout(Duration.ofSeconds(5))
        .build();
}
```

#### 8.2.3 Health Checks

**Spring Boot Actuator**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

**Docker Healthcheck**:
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 40s
```

**Endpoints**:
- Products Service: `http://localhost:8081/actuator/health`
- Inventory Service: `http://localhost:8082/actuator/health`
- Orders Service: `http://localhost:8083/actuator/health`
- Notifications Service: `http://localhost:8084/actuator/health`

#### 8.2.4 Timeouts

**Nginx**:
```nginx
proxy_connect_timeout 60s;
proxy_send_timeout 60s;
proxy_read_timeout 60s;
```

**Spring Cloud Gateway**:
```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 5000
        response-timeout: 10s
```

#### 8.2.5 Bulkhead Pattern (Future)

**Propósito**: Aislar recursos para prevenir agotamiento.

**Implementación con Resilience4j**:
```java
@Bulkhead(name = "productService", type = Bulkhead.Type.THREADPOOL)
public CompletableFuture<Product> getProduct(String sku) {
    return CompletableFuture.supplyAsync(() ->
        productRepository.findBySku(sku)
    );
}
```

#### 8.2.6 Graceful Shutdown

**Spring Boot**:
```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

**Comportamiento**:
1. Servidor deja de aceptar nuevas peticiones
2. Completa peticiones en curso (hasta 30s)
3. Cierra conexiones de base de datos
4. Termina el proceso

---

## 9. Despliegue y DevOps

### 9.1 Containerización con Docker

#### 9.1.1 Multi-Stage Build (Frontend)

```dockerfile
# Stage 1: Build
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build --prod

# Stage 2: Production
FROM nginx:alpine
COPY --from=builder /app/dist/frontend-angular/browser /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Beneficios**:
- ✅ Imagen final pequeña (~50MB vs ~1GB)
- ✅ No incluye dependencias de desarrollo
- ✅ Más segura (menos superficie de ataque)

#### 9.1.2 Multi-Stage Build (Backend)

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2: Production
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Optimizaciones**:
- ✅ Layer caching (dependencies cached separately)
- ✅ JRE en lugar de JDK (imagen más pequeña)
- ✅ Alpine Linux (imagen base mínima)

#### 9.1.3 Docker Compose

**Estructura**:
```yaml
version: '3.8'

services:
  # Infrastructure
  eureka-server:
    build: ./microservices_linktic/eureka-server
    ports: ["8761:8761"]
    networks: [linktic-network]

  api-gateway:
    build: ./microservices_linktic/api-gateway
    ports: ["8085:8080"]
    depends_on: [eureka-server]
    environment:
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/

  # Databases
  postgres_db:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: linktic_user
      POSTGRES_PASSWORD: linktic_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-databases.sql:/docker-entrypoint-initdb.d/init.sql

  # Microservices
  products-service:
    build: ./microservices_linktic/products_service
    depends_on: [eureka-server, postgres_db]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres_db:5432/products_db
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/

  # Frontend
  frontend:
    build: ./frontend_angular
    ports: ["4200:80"]
    depends_on: [api-gateway]

networks:
  linktic-network:
    driver: bridge

volumes:
  postgres_data:
  mysql_data:
```

**Ventajas**:
- ✅ Entorno reproducible
- ✅ Orquestación automática
- ✅ Networking simplificado
- ✅ Gestión de volúmenes

### 9.2 Arquitectura de Despliegue

```mermaid
graph TB
    subgraph "Docker Host"
        subgraph "linktic-network (Bridge Network)"

            subgraph "Frontend Layer"
                Frontend[Frontend Container<br/>nginx:alpine<br/>Port: 4200:80]
            end

            subgraph "Gateway Layer"
                Gateway[API Gateway<br/>Spring Boot<br/>Port: 8085:8080]
                Eureka[Eureka Server<br/>Spring Boot<br/>Port: 8761:8761]
            end

            subgraph "Business Layer"
                Products[Products Service<br/>Spring Boot<br/>Port: 8081:8081]
                Inventory[Inventory Service<br/>Spring Boot<br/>Port: 8082:8082]
                Orders[Orders Service<br/>Spring Boot<br/>Port: 8083:8083]
                Notifications[Notifications Service<br/>Spring Boot<br/>Port: 8084:8084]
            end

            subgraph "Data Layer"
                Postgres[(PostgreSQL<br/>Port: 5432:5432)]
                MySQL[(MySQL<br/>Port: 3306:3306)]
            end

            subgraph "Messaging Layer"
                Kafka[Kafka<br/>Port: 9092:9092]
                Zookeeper[Zookeeper<br/>Port: 2181:2181]
            end
        end

        subgraph "Volumes"
            PGData[postgres_data]
            MySQLData[mysql_data]
        end
    end

    Internet((Internet)) -->|Port 4200| Frontend
    Internet -->|Port 8085| Gateway

    Frontend --> Gateway
    Gateway --> Eureka
    Gateway --> Products
    Gateway --> Inventory
    Gateway --> Orders

    Products --> Eureka
    Inventory --> Eureka
    Orders --> Eureka
    Notifications --> Eureka

    Products --> Postgres
    Inventory --> Postgres
    Notifications --> Postgres
    Orders --> MySQL

    Orders --> Kafka
    Kafka --> Notifications
    Kafka --> Zookeeper

    Postgres -.->|Persist| PGData
    MySQL -.->|Persist| MySQLData

    style Frontend fill:#bbdefb
    style Gateway fill:#90caf9
    style Eureka fill:#64b5f6
    style Products fill:#81c784
    style Inventory fill:#81c784
    style Orders fill:#81c784
    style Notifications fill:#81c784
    style Postgres fill:#ffb74d
    style MySQL fill:#ffb74d
    style Kafka fill:#ba68c8
    style Zookeeper fill:#9c27b0
```

### 9.3 Estrategia de Despliegue

#### 9.3.1 Orden de Inicio

```bash
# 1. Infrastructure services
docker-compose up -d zookeeper kafka postgres_db mysql_db

# 2. Service Discovery
docker-compose up -d eureka-server

# Wait for Eureka to be ready (30 seconds)

# 3. Microservices
docker-compose up -d products-service inventory-service orders-service notifications-service

# 4. API Gateway
docker-compose up -d api-gateway

# 5. Frontend
docker-compose up -d frontend
```

**Automatizado con `depends_on` y `healthcheck`**:
```yaml
products-service:
  depends_on:
    eureka-server:
      condition: service_healthy
    postgres_db:
      condition: service_healthy
```

#### 9.3.2 Health Checks

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 40s
```

**Estados**:
- `starting`: Contenedor iniciando (dentro de `start_period`)
- `healthy`: Health check exitoso
- `unhealthy`: Health check fallando

#### 9.3.3 Logs Centralizados

**Ver logs de un servicio**:
```bash
docker-compose logs -f products-service
```

**Ver logs de todos los servicios**:
```bash
docker-compose logs -f
```

**Ver últimas 50 líneas**:
```bash
docker-compose logs --tail=50 products-service
```

### 9.4 Escalabilidad

#### 9.4.1 Escalado Horizontal

**Escalar Products Service a 3 instancias**:
```bash
docker-compose up -d --scale products-service=3
```

**Eureka registra todas las instancias**:
```
PRODUCTS-SERVICE
  - products-service-1:8081
  - products-service-2:8081
  - products-service-3:8081
```

**API Gateway balancea automáticamente**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: products-service
          uri: lb://PRODUCTS-SERVICE  # Load balanced
```

#### 9.4.2 Escalado Vertical

**Aumentar recursos de un contenedor**:
```yaml
products-service:
  deploy:
    resources:
      limits:
        cpus: '2.0'
        memory: 2G
      reservations:
        cpus: '1.0'
        memory: 1G
```

#### 9.4.3 Estrategia de Escalado

| Servicio | Estrategia | Razón |
|----------|-----------|-------|
| **Products Service** | Horizontal | Alto tráfico de lectura |
| **Orders Service** | Horizontal | Picos de compras |
| **Notifications Service** | Horizontal | Procesamiento asíncrono |
| **API Gateway** | Horizontal | Punto de entrada único |
| **Eureka** | Vertical | Registro centralizado |
| **Kafka** | Horizontal (partitions) | Alto throughput |
| **PostgreSQL** | Vertical + Read Replicas | Consistencia de datos |

### 9.5 Monitoreo y Observabilidad

#### 9.5.1 Métricas con Spring Boot Actuator

**Endpoints disponibles**:
- `/actuator/health` - Estado del servicio
- `/actuator/info` - Información del servicio
- `/actuator/metrics` - Métricas de JVM, HTTP, etc.
- `/actuator/prometheus` - Métricas en formato Prometheus

**Configuración**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

#### 9.5.2 Stack de Monitoreo (Future)

```
Prometheus → Scrape metrics from /actuator/prometheus
     ↓
Grafana → Visualize dashboards
     ↓
Alertmanager → Send alerts
```

#### 9.5.3 Distributed Tracing (Future)

**Spring Cloud Sleuth + Zipkin**:
```yaml
spring:
  sleuth:
    sampler:
      probability: 1.0
  zipkin:
    base-url: http://zipkin:9411
```

**Trace ID propagation**:
```
Frontend → [trace-id: abc123] → API Gateway
                                      ↓ [trace-id: abc123]
                                Products Service
                                      ↓ [trace-id: abc123]
                                PostgreSQL
```

#### 9.5.4 Logging Strategy

**Niveles de Log**:
```yaml
logging:
  level:
    root: INFO
    com.linktic_test: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

**Formato JSON** (para ELK Stack):
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
</dependency>
```

### 9.6 Backup y Recuperación

#### 9.6.1 Backup de Bases de Datos

**PostgreSQL**:
```bash
# Backup
docker exec postgres_db pg_dump -U linktic_user products_db > backup_products.sql

# Restore
docker exec -i postgres_db psql -U linktic_user products_db < backup_products.sql
```

**MySQL**:
```bash
# Backup
docker exec mysql_db mysqldump -u linktic_user -p orders_db > backup_orders.sql

# Restore
docker exec -i mysql_db mysql -u linktic_user -p orders_db < backup_orders.sql
```

#### 9.6.2 Volúmenes Persistentes

```yaml
volumes:
  postgres_data:
    driver: local
  mysql_data:
    driver: local
```

**Backup de volúmenes**:
```bash
docker run --rm -v postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz /data
```

### 9.7 CI/CD Pipeline (Future)

```mermaid
graph LR
    A[Git Push] --> B[GitHub Actions]
    B --> C[Run Tests]
    C --> D{Tests Pass?}
    D -->|No| E[Notify Developer]
    D -->|Yes| F[Build Docker Images]
    F --> G[Push to Registry]
    G --> H[Deploy to Staging]
    H --> I[Integration Tests]
    I --> J{Tests Pass?}
    J -->|No| E
    J -->|Yes| K[Deploy to Production]
    K --> L[Health Check]
    L --> M{Healthy?}
    M -->|No| N[Rollback]
    M -->|Yes| O[Success]
```

**GitHub Actions Workflow**:
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run tests
        run: mvn test

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - name: Build Docker images
        run: docker-compose build
      - name: Push to registry
        run: docker-compose push

  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to production
        run: |
          ssh user@server 'docker-compose pull'
          ssh user@server 'docker-compose up -d'
```

---

## 10. Mejores Prácticas y Recomendaciones

### 10.1 Código

#### 10.1.1 SOLID Principles

**Single Responsibility Principle**:
```java
// ❌ Bad: Controller doing too much
@RestController
public class ProductController {
    public Product createProduct(ProductRequest request) {
        // Validation
        // Business logic
        // Database access
        // Email sending
    }
}

// ✅ Good: Separation of concerns
@RestController
public class ProductController {
    private final ProductService productService;

    public ResponseEntity<ProductResponse> createProduct(@Valid ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }
}

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest request) {
        // Business logic only
    }
}
```

**Open/Closed Principle**:
```java
// ✅ Open for extension, closed for modification
public interface NotificationStrategy {
    void send(Notification notification);
}

@Service
public class EmailNotificationStrategy implements NotificationStrategy {
    public void send(Notification notification) {
        // Email implementation
    }
}

@Service
public class SMSNotificationStrategy implements NotificationStrategy {
    public void send(Notification notification) {
        // SMS implementation
    }
}
```

**Dependency Inversion Principle**:
```java
// ✅ Depend on abstractions, not concretions
@Service
public class OrderService {
    private final OrderRepository orderRepository;  // Interface
    private final KafkaTemplate kafkaTemplate;      // Abstraction

    // High-level module doesn't depend on low-level modules
}
```

#### 10.1.2 Clean Code

**Nombres descriptivos**:
```java
// ❌ Bad
public List<Product> get(int p) {
    return repo.find(p);
}

// ✅ Good
public Page<Product> getActiveProducts(int pageNumber) {
    return productRepository.findByStatusTrue(PageRequest.of(pageNumber, 10));
}
```

**Funciones pequeñas**:
```java
// ✅ Each function does one thing
public OrderResponse createOrder(OrderRequest request) {
    String orderNumber = generateOrderNumber();
    BigDecimal totalAmount = calculateTotalAmount(request);
    Order order = buildOrder(request, orderNumber, totalAmount);
    Order savedOrder = saveOrder(order);
    publishOrderEvent(savedOrder);
    return mapToResponse(savedOrder);
}
```

**Evitar magic numbers**:
```java
// ❌ Bad
if (products.size() > 100) { ... }

// ✅ Good
private static final int MAX_PRODUCTS_PER_PAGE = 100;
if (products.size() > MAX_PRODUCTS_PER_PAGE) { ... }
```

#### 10.1.3 Error Handling

**Global Exception Handler**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));

        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors,
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }
}
```

**Custom Exceptions**:
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
```

#### 10.1.4 Testing

**Unit Tests**:
```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_WhenSkuIsUnique_ShouldReturnProduct() {
        // Given
        ProductRequest request = new ProductRequest("SKU001", "Product", "",
                                                     BigDecimal.TEN, true);
        when(productRepository.existsBySku("SKU001")).thenReturn(false);
        when(productRepository.save(any())).thenReturn(new Product());

        // When
        ProductResponse response = productService.createProduct(request);

        // Then
        assertNotNull(response);
        verify(productRepository).save(any());
    }

    @Test
    void createProduct_WhenSkuExists_ShouldThrowException() {
        // Given
        ProductRequest request = new ProductRequest("SKU001", "Product", "",
                                                     BigDecimal.TEN, true);
        when(productRepository.existsBySku("SKU001")).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class,
                    () -> productService.createProduct(request));
    }
}
```

**Integration Tests**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createProduct_ShouldReturn201() throws Exception {
        String requestBody = """
            {
                "sku": "TEST001",
                "name": "Test Product",
                "price": 99.99,
                "status": true
            }
            """;

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.sku").value("TEST001"));
    }
}
```

### 10.2 Base de Datos

#### 10.2.1 Índices

```sql
-- ✅ Índices en columnas frecuentemente consultadas
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_orders_customer_email ON orders(customer_email);
CREATE INDEX idx_orders_status ON orders(status);
```

#### 10.2.2 Constraints

```sql
-- ✅ Constraints para integridad de datos
ALTER TABLE products ADD CONSTRAINT uk_products_sku UNIQUE (sku);
ALTER TABLE products ADD CONSTRAINT chk_products_price CHECK (price >= 0);
ALTER TABLE inventory ADD CONSTRAINT chk_inventory_quantity
    CHECK (quantity >= reserved_quantity);
```

#### 10.2.3 Transacciones

```java
// ✅ Usar @Transactional apropiadamente
@Transactional(readOnly = true)  // Optimización para lecturas
public Page<Product> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable);
}

@Transactional(isolation = Isolation.SERIALIZABLE)  // Para operaciones críticas
public Product createProduct(ProductRequest request) {
    // Ensures SKU uniqueness even under concurrent access
}
```

### 10.3 API Design

#### 10.3.1 RESTful Conventions

| Método | Endpoint | Descripción | Status Code |
|--------|----------|-------------|-------------|
| GET | `/api/v1/products` | Listar productos | 200 OK |
| GET | `/api/v1/products/{sku}` | Obtener producto | 200 OK, 404 Not Found |
| POST | `/api/v1/products` | Crear producto | 201 Created, 400 Bad Request, 409 Conflict |
| PUT | `/api/v1/products/{sku}` | Actualizar producto | 200 OK, 404 Not Found |
| DELETE | `/api/v1/products/{sku}` | Eliminar producto | 204 No Content, 404 Not Found |

#### 10.3.2 Versionado de API

```java
// ✅ Versión en la URL
@RequestMapping("/api/v1/products")

// Future: v2 con cambios breaking
@RequestMapping("/api/v2/products")
```

#### 10.3.3 Paginación

```java
// ✅ Usar Pageable de Spring
@GetMapping
public ResponseEntity<Page<ProductResponse>> getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id,desc") String[] sort) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
    return ResponseEntity.ok(productService.getAllProducts(pageable));
}
```

**Response**:
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 5,
  "totalElements": 50,
  "first": true,
  "last": false
}
```

#### 10.3.4 Filtrado y Búsqueda

```java
// ✅ Query parameters para filtros
@GetMapping
public Page<Product> getProducts(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean status,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        Pageable pageable) {

    return productService.searchProducts(name, status, minPrice, maxPrice, pageable);
}
```

### 10.4 Performance

#### 10.4.1 Lazy Loading

```java
@Entity
public class Order {
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items;  // Loaded only when accessed
}
```

#### 10.4.2 Caching (Future)

```java
@Cacheable(value = "products", key = "#sku")
public Product getProductBySku(String sku) {
    return productRepository.findBySku(sku)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
}

@CacheEvict(value = "products", key = "#sku")
public void updateProduct(String sku, ProductRequest request) {
    // Update logic
}
```

#### 10.4.3 Connection Pooling

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 10.5 Seguridad

#### 10.5.1 Secrets Management

```yaml
# ❌ Bad: Hardcoded credentials
spring:
  datasource:
    password: mypassword123

# ✅ Good: Environment variables
spring:
  datasource:
    password: ${DB_PASSWORD}
```

#### 10.5.2 Input Sanitization

```java
// ✅ Always validate and sanitize input
@PostMapping
public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody ProductRequest request) {  // @Valid triggers validation

    // Additional sanitization
    String sanitizedSku = request.getSku().trim().toUpperCase();

    return ResponseEntity.ok(productService.createProduct(request));
}
```

---

## 11. Conclusiones y Roadmap

### 11.1 Estado Actual del Sistema

✅ **Implementado**:
- Arquitectura de microservicios con 4 servicios
- Service Discovery con Eureka
- API Gateway con Spring Cloud Gateway
- Event-driven architecture con Kafka
- Frontend Angular con Clean Architecture
- Containerización con Docker
- Health checks y monitoring básico
- Validación de datos en frontend y backend
- Manejo de errores centralizado
- Tests unitarios e integración (76/76 passing)

### 11.2 Roadmap de Mejoras

#### Fase 1: Seguridad (1-2 meses)
- [ ] Implementar JWT Authentication
- [ ] Agregar OAuth 2.0 / OpenID Connect
- [ ] Rate Limiting en API Gateway
- [ ] HTTPS/TLS en producción
- [ ] Secret Management con Vault

#### Fase 2: Observabilidad (1 mes)
- [ ] Prometheus + Grafana para métricas
- [ ] ELK Stack para logs centralizados
- [ ] Distributed Tracing con Zipkin/Jaeger
- [ ] Alerting con Alertmanager

#### Fase 3: Resiliencia (1 mes)
- [ ] Implementar Saga Pattern para transacciones distribuidas
- [ ] Bulkhead Pattern con Resilience4j
- [ ] Retry policies configurables
- [ ] Chaos Engineering con Chaos Monkey

#### Fase 4: Performance (1-2 meses)
- [ ] Redis para caching
- [ ] CDN para assets estáticos
- [ ] Database read replicas
- [ ] Query optimization
- [ ] Connection pooling tuning

#### Fase 5: CI/CD (1 mes)
- [ ] GitHub Actions pipeline completo
- [ ] Automated testing en pipeline
- [ ] Blue-Green deployment
- [ ] Canary releases
- [ ] Automated rollback

#### Fase 6: Features (Ongoing)
- [ ] Inventory Service integration con Orders
- [ ] Payment Service
- [ ] Shipping Service
- [ ] User Management Service
- [ ] Reviews and Ratings
- [ ] Recommendation Engine

### 11.3 Métricas de Éxito

| Métrica | Objetivo | Actual |
|---------|----------|--------|
| **Uptime** | 99.9% | - |
| **Response Time (p95)** | < 200ms | - |
| **Error Rate** | < 0.1% | - |
| **Test Coverage** | > 80% | 100% (76/76 tests) |
| **Deployment Frequency** | Daily | Manual |
| **Mean Time to Recovery** | < 1 hour | - |

### 11.4 Lecciones Aprendidas

1. **Clean Architecture** proporciona excelente separación de concerns
2. **Microservicios** permiten escalabilidad independiente pero aumentan complejidad
3. **Event-driven** desacopla servicios pero requiere manejo cuidadoso de eventos
4. **Docker Compose** excelente para desarrollo local, Kubernetes para producción
5. **Testing** es crítico para confianza en despliegues

---

## 12. Referencias y Recursos

### 12.1 Documentación Oficial

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Angular Documentation](https://angular.io/docs)
- [Docker Documentation](https://docs.docker.com/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)

### 12.2 Libros Recomendados

- **"Clean Architecture"** - Robert C. Martin
- **"Domain-Driven Design"** - Eric Evans
- **"Microservices Patterns"** - Chris Richardson
- **"Building Microservices"** - Sam Newman
- **"Release It!"** - Michael T. Nygard

### 12.3 Patrones y Prácticas

- [Microservices.io](https://microservices.io/) - Patrones de microservicios
- [12 Factor App](https://12factor.net/) - Metodología para aplicaciones cloud-native
- [C4 Model](https://c4model.com/) - Diagramas de arquitectura
- [Martin Fowler's Blog](https://martinfowler.com/) - Arquitectura de software

---

**Documento creado por**: Arquitecto de Software Senior
**Fecha**: Noviembre 2025
**Versión**: 1.0
**Proyecto**: Linktic E-Commerce Platform


