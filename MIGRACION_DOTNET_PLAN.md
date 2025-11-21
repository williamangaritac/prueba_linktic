# Plan de Migración: Java Spring Boot → .NET 8

## 1. Resumen Ejecutivo

Este documento describe el plan detallado para migrar el sistema de E-Commerce de microservicios desde **Java Spring Boot + Angular** a **.NET 8 + Angular**, manteniendo **exactamente la misma arquitectura**.

### Objetivo
Replicar la funcionalidad completa del sistema manteniendo:
- ✅ La misma arquitectura de microservicios
- ✅ Los mismos endpoints REST
- ✅ Las mismas bases de datos (PostgreSQL, MySQL)
- ✅ El mismo frontend Angular (sin cambios)
- ✅ Los mismos patrones de diseño
- ✅ La misma comunicación asíncrona (Kafka)

---

## 2. Mapeo de Tecnologías: Java → .NET

| Componente Java | Tecnología Java | Equivalente .NET 8 |
|-----------------|-----------------|-------------------|
| **Framework Base** | Spring Boot 3.5.7 | ASP.NET Core 8.0 |
| **ORM** | Spring Data JPA + Hibernate | Entity Framework Core 8.0 |
| **API REST** | Spring Web MVC | ASP.NET Core Web API |
| **Service Discovery** | Netflix Eureka | Steeltoe Eureka Client |
| **API Gateway** | Spring Cloud Gateway | Ocelot o YARP (Reverse Proxy) |
| **Circuit Breaker** | Resilience4j | Polly |
| **Messaging** | Spring Kafka | Confluent.Kafka / MassTransit |
| **Dependency Injection** | Spring IoC Container | Built-in DI Container |
| **Configuration** | application.yml | appsettings.json |
| **Validation** | Jakarta Validation | Data Annotations / FluentValidation |
| **OpenAPI/Swagger** | SpringDoc OpenAPI | Swashbuckle.AspNetCore |
| **Logging** | SLF4J + Logback | ILogger / Serilog |
| **Testing** | JUnit 5 + Mockito | xUnit / NUnit + Moq |
| **Email** | Spring Mail | MailKit / FluentEmail |

---

## 3. Estructura de la Solución .NET

### 3.1 Estructura de Directorios

```
microservices_dotnet/
├── Linktic.sln                                    # Solución principal
│
├── src/
│   ├── Services/
│   │   ├── Linktic.Products/
│   │   │   ├── Linktic.Products.Api/             # ASP.NET Core Web API
│   │   │   ├── Linktic.Products.Domain/          # Entidades de dominio
│   │   │   ├── Linktic.Products.Application/     # Lógica de negocio
│   │   │   └── Linktic.Products.Infrastructure/  # EF Core, repositorios
│   │   │
│   │   ├── Linktic.Orders/
│   │   │   ├── Linktic.Orders.Api/
│   │   │   ├── Linktic.Orders.Domain/
│   │   │   ├── Linktic.Orders.Application/
│   │   │   └── Linktic.Orders.Infrastructure/
│   │   │
│   │   ├── Linktic.Inventory/
│   │   │   ├── Linktic.Inventory.Api/
│   │   │   ├── Linktic.Inventory.Domain/
│   │   │   ├── Linktic.Inventory.Application/
│   │   │   └── Linktic.Inventory.Infrastructure/
│   │   │
│   │   └── Linktic.Notifications/
│   │       ├── Linktic.Notifications.Api/
│   │       ├── Linktic.Notifications.Domain/
│   │       ├── Linktic.Notifications.Application/
│   │       └── Linktic.Notifications.Infrastructure/
│   │
│   ├── Gateway/
│   │   └── Linktic.ApiGateway/                   # Ocelot Gateway
│   │
│   ├── ServiceDiscovery/
│   │   └── Linktic.EurekaServer/                 # Steeltoe Eureka (opcional)
│   │
│   └── Shared/
│       ├── Linktic.Common/                       # DTOs compartidos
│       ├── Linktic.Contracts/                    # Contratos de eventos
│       └── Linktic.Infrastructure.Shared/        # Helpers comunes
│
└── tests/
    ├── Linktic.Products.Tests/
    ├── Linktic.Orders.Tests/
    ├── Linktic.Inventory.Tests/
    └── Linktic.Notifications.Tests/
```

### 3.2 Arquitectura por Microservicio (Clean Architecture)

Cada microservicio seguirá **Clean Architecture** con 4 capas:

```
Linktic.{Service}.Api/
├── Controllers/                    # Endpoints REST
├── Middleware/                     # Error handling, logging
├── Program.cs                      # Entry point, DI setup
├── appsettings.json               # Configuración
└── Dockerfile

Linktic.{Service}.Application/
├── Services/                       # Lógica de negocio
├── DTOs/                          # Data Transfer Objects
├── Interfaces/                    # Contratos de servicios
├── Mappings/                      # AutoMapper profiles
└── Validators/                    # FluentValidation

Linktic.{Service}.Domain/
├── Entities/                      # Entidades de dominio
├── Enums/                         # Enumeraciones
└── Interfaces/                    # Contratos de repositorios

Linktic.{Service}.Infrastructure/
├── Data/
│   ├── ApplicationDbContext.cs   # EF Core DbContext
│   └── Migrations/
├── Repositories/                  # Implementación de repositorios
├── Kafka/                         # Productores/consumidores
└── Email/                         # Servicios de email
```

---

## 4. Migración por Microservicio

### 4.1 Products Service

#### Entidades

**Java (Product.java)**
```java
@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Boolean status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

**C# (Product.cs)**
```csharp
public class Product
{
    public long Id { get; set; }

    [Required]
    [MaxLength(100)]
    public string Sku { get; set; }

    [Required]
    [MaxLength(255)]
    public string Name { get; set; }

    [MaxLength(1000)]
    public string Description { get; set; }

    [Required]
    [Column(TypeName = "decimal(10,2)")]
    public decimal Price { get; set; }

    public bool Status { get; set; } = true;

    public DateTime CreatedAt { get; set; }
    public DateTime UpdatedAt { get; set; }
}
```

#### DbContext (EF Core)

```csharp
public class ProductsDbContext : DbContext
{
    public ProductsDbContext(DbContextOptions<ProductsDbContext> options)
        : base(options) { }

    public DbSet<Product> Products { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Product>(entity =>
        {
            entity.ToTable("products");
            entity.HasKey(e => e.Id);
            entity.HasIndex(e => e.Sku).IsUnique();
            entity.Property(e => e.CreatedAt).HasDefaultValueSql("CURRENT_TIMESTAMP");
        });
    }

    public override int SaveChanges()
    {
        UpdateTimestamps();
        return base.SaveChanges();
    }

    private void UpdateTimestamps()
    {
        var entries = ChangeTracker.Entries()
            .Where(e => e.Entity is Product &&
                   (e.State == EntityState.Added || e.State == EntityState.Modified));

        foreach (var entry in entries)
        {
            var entity = (Product)entry.Entity;
            if (entry.State == EntityState.Added)
                entity.CreatedAt = DateTime.UtcNow;
            entity.UpdatedAt = DateTime.UtcNow;
        }
    }
}
```

#### Repository Pattern

**Interface (IProductRepository.cs)**
```csharp
public interface IProductRepository
{
    Task<Product> GetByIdAsync(long id);
    Task<Product> GetBySkuAsync(string sku);
    Task<PagedResult<Product>> GetAllAsync(int page, int size);
    Task<List<Product>> GetActiveProductsAsync();
    Task<Product> CreateAsync(Product product);
    Task<Product> UpdateAsync(Product product);
    Task DeleteAsync(long id);
    Task<bool> ExistsBySkuAsync(string sku);
}
```

**Implementation (ProductRepository.cs)**
```csharp
public class ProductRepository : IProductRepository
{
    private readonly ProductsDbContext _context;

    public ProductRepository(ProductsDbContext context)
    {
        _context = context;
    }

    public async Task<Product> GetByIdAsync(long id)
    {
        return await _context.Products
            .FirstOrDefaultAsync(p => p.Id == id);
    }

    public async Task<Product> GetBySkuAsync(string sku)
    {
        return await _context.Products
            .FirstOrDefaultAsync(p => p.Sku == sku);
    }

    public async Task<PagedResult<Product>> GetAllAsync(int page, int size)
    {
        var query = _context.Products.AsQueryable();

        var totalItems = await query.CountAsync();
        var items = await query
            .OrderByDescending(p => p.CreatedAt)
            .Skip(page * size)
            .Take(size)
            .ToListAsync();

        return new PagedResult<Product>
        {
            Items = items,
            TotalItems = totalItems,
            PageNumber = page,
            PageSize = size,
            TotalPages = (int)Math.Ceiling(totalItems / (double)size)
        };
    }

    public async Task<Product> CreateAsync(Product product)
    {
        _context.Products.Add(product);
        await _context.SaveChangesAsync();
        return product;
    }

    // ... otros métodos
}
```

#### Service Layer

```csharp
public class ProductService : IProductService
{
    private readonly IProductRepository _repository;
    private readonly IMapper _mapper;
    private readonly ILogger<ProductService> _logger;

    public ProductService(
        IProductRepository repository,
        IMapper mapper,
        ILogger<ProductService> logger)
    {
        _repository = repository;
        _mapper = mapper;
        _logger = logger;
    }

    public async Task<ProductResponse> CreateProductAsync(ProductRequest request)
    {
        // Validar SKU único
        if (await _repository.ExistsBySkuAsync(request.Sku))
            throw new BusinessException($"Product with SKU {request.Sku} already exists");

        var product = _mapper.Map<Product>(request);
        var created = await _repository.CreateAsync(product);

        _logger.LogInformation("Product created: {Sku}", created.Sku);

        return _mapper.Map<ProductResponse>(created);
    }

    public async Task<PagedResponse<ProductResponse>> GetProductsAsync(int page, int size)
    {
        var pagedResult = await _repository.GetAllAsync(page, size);

        return new PagedResponse<ProductResponse>
        {
            Content = _mapper.Map<List<ProductResponse>>(pagedResult.Items),
            TotalPages = pagedResult.TotalPages,
            TotalElements = pagedResult.TotalItems,
            Number = pagedResult.PageNumber,
            Size = pagedResult.PageSize
        };
    }

    // ... otros métodos
}
```

#### Controller

```csharp
[ApiController]
[Route("api/v1/products")]
public class ProductsController : ControllerBase
{
    private readonly IProductService _productService;
    private readonly ILogger<ProductsController> _logger;

    public ProductsController(
        IProductService productService,
        ILogger<ProductsController> logger)
    {
        _productService = productService;
        _logger = logger;
    }

    [HttpPost]
    [ProducesResponseType(typeof(ProductResponse), StatusCodes.Status201Created)]
    public async Task<ActionResult<ProductResponse>> CreateProduct(
        [FromBody] ProductRequest request)
    {
        var product = await _productService.CreateProductAsync(request);
        return CreatedAtAction(nameof(GetProductById), new { id = product.Id }, product);
    }

    [HttpGet]
    [ProducesResponseType(typeof(PagedResponse<ProductResponse>), StatusCodes.Status200OK)]
    public async Task<ActionResult<PagedResponse<ProductResponse>>> GetProducts(
        [FromQuery] int page = 0,
        [FromQuery] int size = 6)
    {
        var products = await _productService.GetProductsAsync(page, size);
        return Ok(products);
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<ProductResponse>> GetProductById(long id)
    {
        var product = await _productService.GetProductByIdAsync(id);
        return Ok(product);
    }

    // ... otros endpoints
}
```

#### Program.cs (DI Setup)

```csharp
var builder = WebApplication.CreateBuilder(args);

// Add services to the container
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Database
builder.Services.AddDbContext<ProductsDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("ProductsDb")));

// AutoMapper
builder.Services.AddAutoMapper(typeof(Program));

// Repositories
builder.Services.AddScoped<IProductRepository, ProductRepository>();

// Services
builder.Services.AddScoped<IProductService, ProductService>();

// Eureka Discovery
builder.Services.AddServiceDiscovery(o => o.UseEureka());

// CORS
builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.WithOrigins("http://localhost:4200")
              .AllowAnyMethod()
              .AllowAnyHeader()
              .AllowCredentials();
    });
});

var app = builder.Build();

// Configure the HTTP request pipeline
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors();
app.UseAuthorization();
app.MapControllers();

app.Run();
```

#### appsettings.json

```json
{
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft.AspNetCore": "Warning"
    }
  },
  "ConnectionStrings": {
    "ProductsDb": "Host=localhost;Port=5432;Database=linktic_products;Username=postgres;Password="
  },
  "Eureka": {
    "Client": {
      "ServiceUrl": "http://localhost:8761/eureka/",
      "ShouldRegisterWithEureka": true,
      "ShouldFetchRegistry": true
    },
    "Instance": {
      "AppName": "products-service",
      "Port": 8081,
      "HostName": "localhost",
      "PreferIpAddress": true
    }
  },
  "AllowedHosts": "*"
}
```

---

### 4.2 Orders Service

#### Diferencias clave:
- **MySQL en lugar de PostgreSQL**
- **Integración con Kafka** (publicar eventos)
- **HTTP Client** para llamar a Inventory Service
- **Relación 1:N** (Order → OrderItems)

#### Entidades

```csharp
public class Order
{
    public long Id { get; set; }

    [Required]
    [MaxLength(255)]
    public string OrderNumber { get; set; }

    [Required]
    [Column(TypeName = "decimal(10,2)")]
    public decimal TotalAmount { get; set; }

    [MaxLength(50)]
    public string Status { get; set; } = "PENDING";

    public string CustomerEmail { get; set; }

    public DateTime CreatedAt { get; set; }
    public DateTime UpdatedAt { get; set; }

    // Relación 1:N
    public virtual ICollection<OrderItem> OrderItems { get; set; }
}

public class OrderItem
{
    public long Id { get; set; }

    public long OrderId { get; set; }
    public virtual Order Order { get; set; }

    [Required]
    public string Sku { get; set; }

    public string ProductName { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal Price { get; set; }

    public int Quantity { get; set; }

    [Column(TypeName = "decimal(10,2)")]
    public decimal Subtotal { get; set; }
}
```

#### Kafka Producer

```csharp
public class OrderEventProducer : IOrderEventProducer
{
    private readonly IProducer<string, string> _producer;
    private readonly ILogger<OrderEventProducer> _logger;
    private readonly string _topic;

    public OrderEventProducer(IConfiguration configuration, ILogger<OrderEventProducer> logger)
    {
        var config = new ProducerConfig
        {
            BootstrapServers = configuration["Kafka:BootstrapServers"]
        };

        _producer = new ProducerBuilder<string, string>(config).Build();
        _topic = configuration["Kafka:Topics:OrderEvents"];
        _logger = logger;
    }

    public async Task PublishOrderEventAsync(OrderEventDto orderEvent)
    {
        try
        {
            var json = JsonSerializer.Serialize(orderEvent);
            var message = new Message<string, string>
            {
                Key = orderEvent.OrderNumber,
                Value = json
            };

            var result = await _producer.ProduceAsync(_topic, message);

            _logger.LogInformation(
                "Order event published: {OrderNumber} to partition {Partition}",
                orderEvent.OrderNumber, result.Partition);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error publishing order event");
            throw;
        }
    }
}
```

#### HTTP Client (Inventory Service)

```csharp
public class InventoryServiceClient : IInventoryServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly ILogger<InventoryServiceClient> _logger;

    public InventoryServiceClient(HttpClient httpClient, ILogger<InventoryServiceClient> logger)
    {
        _httpClient = httpClient;
        _logger = logger;
    }

    public async Task<bool> UpdateInventoryAsync(string sku, int quantity)
    {
        try
        {
            var request = new InventoryUpdateRequest
            {
                Sku = sku,
                Quantity = quantity
            };

            var response = await _httpClient.PutAsJsonAsync(
                $"/api/v1/inventory/update", request);

            return response.IsSuccessStatusCode;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating inventory for SKU: {Sku}", sku);
            return false;
        }
    }
}
```

#### OrderService con Kafka

```csharp
public class OrderService : IOrderService
{
    private readonly IOrderRepository _repository;
    private readonly IInventoryServiceClient _inventoryClient;
    private readonly IOrderEventProducer _eventProducer;
    private readonly IMapper _mapper;

    public async Task<OrderResponse> CreateOrderAsync(OrderRequest request)
    {
        // 1. Validar y actualizar inventario
        foreach (var item in request.OrderItems)
        {
            var success = await _inventoryClient.UpdateInventoryAsync(
                item.Sku, item.Quantity);

            if (!success)
                throw new BusinessException($"Insufficient stock for {item.Sku}");
        }

        // 2. Generar número de orden
        var orderNumber = GenerateOrderNumber();

        // 3. Crear orden
        var order = new Order
        {
            OrderNumber = orderNumber,
            CustomerEmail = request.CustomerEmail,
            Status = "PENDING",
            TotalAmount = request.OrderItems.Sum(i => i.Price * i.Quantity),
            OrderItems = request.OrderItems.Select(i => new OrderItem
            {
                Sku = i.Sku,
                ProductName = i.ProductName,
                Price = i.Price,
                Quantity = i.Quantity,
                Subtotal = i.Price * i.Quantity
            }).ToList()
        };

        var created = await _repository.CreateAsync(order);

        // 4. Publicar evento en Kafka
        var orderEvent = new OrderEventDto
        {
            OrderNumber = orderNumber,
            CustomerEmail = request.CustomerEmail,
            OrderItems = request.OrderItems.ToList()
        };

        await _eventProducer.PublishOrderEventAsync(orderEvent);

        return _mapper.Map<OrderResponse>(created);
    }

    private string GenerateOrderNumber()
    {
        var date = DateTime.UtcNow.ToString("yyyyMMdd");
        var random = Guid.NewGuid().ToString("N").Substring(0, 8).ToUpper();
        return $"ORD-{date}-{random}";
    }
}
```

---

### 4.3 Notifications Service

#### Kafka Consumer

```csharp
public class OrderEventConsumer : BackgroundService
{
    private readonly IConsumer<string, string> _consumer;
    private readonly IServiceProvider _serviceProvider;
    private readonly ILogger<OrderEventConsumer> _logger;

    public OrderEventConsumer(
        IConfiguration configuration,
        IServiceProvider serviceProvider,
        ILogger<OrderEventConsumer> logger)
    {
        var config = new ConsumerConfig
        {
            BootstrapServers = configuration["Kafka:BootstrapServers"],
            GroupId = configuration["Kafka:GroupId"],
            AutoOffsetReset = AutoOffsetReset.Earliest
        };

        _consumer = new ConsumerBuilder<string, string>(config).Build();
        _serviceProvider = serviceProvider;
        _logger = logger;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        _consumer.Subscribe("order-events");

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                var consumeResult = _consumer.Consume(stoppingToken);

                var orderEvent = JsonSerializer.Deserialize<OrderEventDto>(
                    consumeResult.Message.Value);

                // Crear scope para DI
                using var scope = _serviceProvider.CreateScope();
                var notificationService = scope.ServiceProvider
                    .GetRequiredService<INotificationService>();

                await notificationService.ProcessOrderEventAsync(orderEvent);

                _logger.LogInformation(
                    "Processed order event: {OrderNumber}",
                    orderEvent.OrderNumber);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error processing order event");
            }
        }
    }

    public override void Dispose()
    {
        _consumer.Close();
        _consumer.Dispose();
        base.Dispose();
    }
}
```

#### Email Service (MailKit)

```csharp
public class EmailService : IEmailService
{
    private readonly IConfiguration _configuration;
    private readonly ILogger<EmailService> _logger;

    public async Task SendOrderConfirmationAsync(string email, string orderNumber)
    {
        try
        {
            var message = new MimeMessage();
            message.From.Add(new MailboxAddress(
                "Linktic E-Commerce",
                _configuration["Email:Username"]));
            message.To.Add(MailboxAddress.Parse(email));
            message.Subject = $"Confirmación de Orden {orderNumber}";

            message.Body = new TextPart("html")
            {
                Text = $@"
                    <h1>¡Gracias por tu compra!</h1>
                    <p>Tu orden <strong>{orderNumber}</strong> ha sido procesada.</p>
                "
            };

            using var client = new SmtpClient();
            await client.ConnectAsync(
                _configuration["Email:Host"],
                int.Parse(_configuration["Email:Port"]),
                SecureSocketOptions.StartTls);

            await client.AuthenticateAsync(
                _configuration["Email:Username"],
                _configuration["Email:Password"]);

            await client.SendAsync(message);
            await client.DisconnectAsync(true);

            _logger.LogInformation("Email sent to {Email}", email);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error sending email to {Email}", email);
            throw;
        }
    }
}
```

---

### 4.4 API Gateway (Ocelot)

#### ocelot.json

```json
{
  "Routes": [
    {
      "DownstreamPathTemplate": "/api/v1/products/{everything}",
      "DownstreamScheme": "http",
      "DownstreamHostAndPorts": [
        {
          "Host": "localhost",
          "Port": 8081
        }
      ],
      "UpstreamPathTemplate": "/api/v1/products/{everything}",
      "UpstreamHttpMethod": [ "Get", "Post", "Put", "Delete" ],
      "ServiceName": "products-service",
      "LoadBalancerOptions": {
        "Type": "RoundRobin"
      },
      "QoSOptions": {
        "ExceptionsAllowedBeforeBreaking": 3,
        "DurationOfBreak": 10000,
        "TimeoutValue": 5000
      }
    },
    {
      "DownstreamPathTemplate": "/api/v1/orders/{everything}",
      "DownstreamScheme": "http",
      "DownstreamHostAndPorts": [
        {
          "Host": "localhost",
          "Port": 8083
        }
      ],
      "UpstreamPathTemplate": "/api/v1/orders/{everything}",
      "UpstreamHttpMethod": [ "Get", "Post", "Put", "Delete" ]
    },
    {
      "DownstreamPathTemplate": "/api/v1/inventory/{everything}",
      "DownstreamScheme": "http",
      "DownstreamHostAndPorts": [
        {
          "Host": "localhost",
          "Port": 8082
        }
      ],
      "UpstreamPathTemplate": "/api/v1/inventory/{everything}",
      "UpstreamHttpMethod": [ "Get", "Post", "Put", "Delete" ]
    }
  ],
  "GlobalConfiguration": {
    "BaseUrl": "http://localhost:8080",
    "ServiceDiscoveryProvider": {
      "Type": "Eureka",
      "Host": "localhost",
      "Port": 8761
    }
  }
}
```

#### Program.cs (Gateway)

```csharp
var builder = WebApplication.CreateBuilder(args);

// Ocelot
builder.Configuration.AddJsonFile("ocelot.json", optional: false, reloadOnChange: true);
builder.Services.AddOcelot(builder.Configuration)
    .AddEureka();

// CORS
builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.WithOrigins("http://localhost:4200")
              .AllowAnyMethod()
              .AllowAnyHeader()
              .AllowCredentials();
    });
});

var app = builder.Build();

app.UseCors();
await app.UseOcelot();

app.Run();
```

---

### 4.5 Eureka Server (.NET)

**Opción 1**: Mantener el Eureka Server de Java (recomendado)
**Opción 2**: Usar Steeltoe Eureka Server (experimental)

Para simplificar, **mantendremos el Eureka Server de Java** y solo los clientes .NET usarán Steeltoe.

#### Steeltoe Client Configuration

```csharp
// Program.cs en cada microservicio
builder.Services.AddServiceDiscovery(o => o.UseEureka());

// appsettings.json
{
  "Eureka": {
    "Client": {
      "ServiceUrl": "http://localhost:8761/eureka/",
      "ShouldRegisterWithEureka": true,
      "ShouldFetchRegistry": true
    },
    "Instance": {
      "AppName": "products-service",
      "Port": 8081,
      "HostName": "localhost",
      "PreferIpAddress": true,
      "LeaseRenewalIntervalInSeconds": 10
    }
  }
}
```

---

## 5. Paquetes NuGet Necesarios

### 5.1 Todos los Servicios

```xml
<PackageReference Include="Microsoft.AspNetCore.OpenApi" Version="8.0.0" />
<PackageReference Include="Swashbuckle.AspNetCore" Version="6.5.0" />
<PackageReference Include="AutoMapper.Extensions.Microsoft.DependencyInjection" Version="12.0.1" />
<PackageReference Include="Serilog.AspNetCore" Version="8.0.0" />
<PackageReference Include="Steeltoe.Discovery.Eureka" Version="3.2.0" />
```

### 5.2 Products, Orders, Inventory Services

```xml
<PackageReference Include="Microsoft.EntityFrameworkCore" Version="8.0.0" />
<PackageReference Include="Npgsql.EntityFrameworkCore.PostgreSQL" Version="8.0.0" /> <!-- PostgreSQL -->
<PackageReference Include="Pomelo.EntityFrameworkCore.MySql" Version="8.0.0" /> <!-- MySQL -->
<PackageReference Include="Microsoft.EntityFrameworkCore.Design" Version="8.0.0" />
```

### 5.3 Orders Service (Kafka)

```xml
<PackageReference Include="Confluent.Kafka" Version="2.3.0" />
```

### 5.4 Notifications Service (Kafka + Email)

```xml
<PackageReference Include="Confluent.Kafka" Version="2.3.0" />
<PackageReference Include="MailKit" Version="4.3.0" />
<PackageReference Include="MimeKit" Version="4.3.0" />
```

### 5.5 API Gateway

```xml
<PackageReference Include="Ocelot" Version="20.0.0" />
<PackageReference Include="Ocelot.Provider.Eureka" Version="20.0.0" />
```

---

## 6. Dockerfiles

### Products Service Dockerfile

```dockerfile
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 8081

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["src/Services/Linktic.Products/Linktic.Products.Api/Linktic.Products.Api.csproj", "Services/Linktic.Products/Linktic.Products.Api/"]
COPY ["src/Services/Linktic.Products/Linktic.Products.Application/Linktic.Products.Application.csproj", "Services/Linktic.Products/Linktic.Products.Application/"]
COPY ["src/Services/Linktic.Products/Linktic.Products.Domain/Linktic.Products.Domain.csproj", "Services/Linktic.Products/Linktic.Products.Domain/"]
COPY ["src/Services/Linktic.Products/Linktic.Products.Infrastructure/Linktic.Products.Infrastructure.csproj", "Services/Linktic.Products/Linktic.Products.Infrastructure/"]

RUN dotnet restore "Services/Linktic.Products/Linktic.Products.Api/Linktic.Products.Api.csproj"
COPY . .
WORKDIR "/src/Services/Linktic.Products/Linktic.Products.Api"
RUN dotnet build "Linktic.Products.Api.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "Linktic.Products.Api.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "Linktic.Products.Api.dll"]
```

---

## 7. docker-compose.yml (Actualizado)

```yaml
version: '3.8'

services:
  # --- Bases de Datos (sin cambios) ---
  postgres:
    image: postgres:15-alpine
    ports:
      - "5432:5432"
    environment:
      POSTGRES_PASSWORD: ""
    volumes:
      - ./database/init-scripts/init-postgres.sql:/docker-entrypoint-initdb.d/init.sql
      - postgres-data:/var/lib/postgresql/data

  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: ""
      MYSQL_ALLOW_EMPTY_PASSWORD: "yes"
    volumes:
      - ./database/init-scripts/init-mysql.sql:/docker-entrypoint-initdb.d/init.sql
      - mysql-data:/var/lib/mysql

  # --- Kafka (sin cambios) ---
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092

  # --- Eureka Server (Java - sin cambios) ---
  eureka-server:
    build: ./microservices_linktic/eureka-server
    ports:
      - "8761:8761"

  # --- API Gateway (.NET) ---
  api-gateway:
    build:
      context: ./microservices_dotnet
      dockerfile: src/Gateway/Linktic.ApiGateway/Dockerfile
    ports:
      - "8080:8080"
    environment:
      - ASPNETCORE_ENVIRONMENT=Production
      - ASPNETCORE_URLS=http://+:8080
    depends_on:
      - eureka-server
      - products-service
      - orders-service
      - inventory-service

  # --- Products Service (.NET) ---
  products-service:
    build:
      context: ./microservices_dotnet
      dockerfile: src/Services/Linktic.Products/Linktic.Products.Api/Dockerfile
    ports:
      - "8081:8081"
    environment:
      - ASPNETCORE_ENVIRONMENT=Production
      - ASPNETCORE_URLS=http://+:8081
      - ConnectionStrings__ProductsDb=Host=postgres;Port=5432;Database=linktic_products;Username=postgres;Password=
      - Eureka__Client__ServiceUrl=http://eureka-server:8761/eureka/
    depends_on:
      - postgres
      - eureka-server

  # --- Inventory Service (.NET) ---
  inventory-service:
    build:
      context: ./microservices_dotnet
      dockerfile: src/Services/Linktic.Inventory/Linktic.Inventory.Api/Dockerfile
    ports:
      - "8082:8082"
    environment:
      - ASPNETCORE_ENVIRONMENT=Production
      - ASPNETCORE_URLS=http://+:8082
      - ConnectionStrings__InventoryDb=Host=postgres;Port=5432;Database=linktic_inventory;Username=postgres;Password=
    depends_on:
      - postgres
      - eureka-server
      - kafka

  # --- Orders Service (.NET) ---
  orders-service:
    build:
      context: ./microservices_dotnet
      dockerfile: src/Services/Linktic.Orders/Linktic.Orders.Api/Dockerfile
    ports:
      - "8083:8083"
    environment:
      - ASPNETCORE_ENVIRONMENT=Production
      - ASPNETCORE_URLS=http://+:8083
      - ConnectionStrings__OrdersDb=Server=mysql;Port=3306;Database=linktic_orders;User=root;Password=;
      - Kafka__BootstrapServers=kafka:9092
      - InventoryService__BaseUrl=http://inventory-service:8082
    depends_on:
      - mysql
      - eureka-server
      - kafka

  # --- Notifications Service (.NET) ---
  notifications-service:
    build:
      context: ./microservices_dotnet
      dockerfile: src/Services/Linktic.Notifications/Linktic.Notifications.Api/Dockerfile
    ports:
      - "8084:8084"
    environment:
      - ASPNETCORE_ENVIRONMENT=Production
      - ASPNETCORE_URLS=http://+:8084
      - Kafka__BootstrapServers=kafka:9092
      - Email__Host=smtp.gmail.com
      - Email__Port=587
      - Email__Username=linktic565@gmail.com
    depends_on:
      - kafka
      - eureka-server

  # --- Frontend Angular (sin cambios) ---
  frontend:
    build: ./frontend_angular
    ports:
      - "4200:80"
    depends_on:
      - api-gateway

volumes:
  postgres-data:
  mysql-data:
```

---

## 8. Migraciones de Base de Datos

### Generar Migración Inicial

```bash
# Products Service
cd src/Services/Linktic.Products/Linktic.Products.Api
dotnet ef migrations add InitialCreate --context ProductsDbContext --output-dir ../Linktic.Products.Infrastructure/Data/Migrations

# Aplicar migración
dotnet ef database update --context ProductsDbContext
```

### Script SQL de Migración

Como las tablas ya existen en PostgreSQL/MySQL (creadas por los scripts init), configuraremos:

```csharp
// Program.cs
using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<ProductsDbContext>();

    // Solo verificar que la BD existe, no crear tablas
    context.Database.EnsureCreated();  // o usar Migrate() si hay cambios
}
```

---

## 9. Testing

### Unit Tests (xUnit + Moq)

```csharp
public class ProductServiceTests
{
    private readonly Mock<IProductRepository> _repositoryMock;
    private readonly Mock<IMapper> _mapperMock;
    private readonly Mock<ILogger<ProductService>> _loggerMock;
    private readonly ProductService _service;

    public ProductServiceTests()
    {
        _repositoryMock = new Mock<IProductRepository>();
        _mapperMock = new Mock<IMapper>();
        _loggerMock = new Mock<ILogger<ProductService>>();
        _service = new ProductService(_repositoryMock.Object, _mapperMock.Object, _loggerMock.Object);
    }

    [Fact]
    public async Task CreateProduct_WithValidData_ShouldReturnProduct()
    {
        // Arrange
        var request = new ProductRequest { Sku = "TEST-001", Name = "Test Product" };
        var product = new Product { Id = 1, Sku = "TEST-001" };

        _repositoryMock.Setup(r => r.ExistsBySkuAsync(It.IsAny<string>()))
            .ReturnsAsync(false);
        _repositoryMock.Setup(r => r.CreateAsync(It.IsAny<Product>()))
            .ReturnsAsync(product);
        _mapperMock.Setup(m => m.Map<Product>(request)).Returns(product);

        // Act
        var result = await _service.CreateProductAsync(request);

        // Assert
        Assert.NotNull(result);
        _repositoryMock.Verify(r => r.CreateAsync(It.IsAny<Product>()), Times.Once);
    }
}
```

---

## 10. Diferencias Clave: Java vs .NET

| Aspecto | Java Spring Boot | .NET 8 ASP.NET Core |
|---------|------------------|---------------------|
| **Configuración** | application.yml | appsettings.json |
| **DI Annotations** | @Autowired, @Service | Constructor injection (built-in) |
| **ORM** | Hibernate (anotaciones @Entity) | EF Core (Fluent API + Data Annotations) |
| **Validation** | @NotBlank, @Size | [Required], [MaxLength] |
| **HTTP Client** | WebClient (reactive) | HttpClient (async/await) |
| **Async** | CompletableFuture | Task<T>, async/await |
| **Logging** | SLF4J | ILogger<T> |
| **Testing** | JUnit + Mockito | xUnit/NUnit + Moq |
| **Kafka** | spring-kafka | Confluent.Kafka |
| **Email** | Spring Mail | MailKit |
| **Circuit Breaker** | Resilience4j | Polly |
| **API Gateway** | Spring Cloud Gateway | Ocelot / YARP |

---

## 11. Plan de Ejecución

### Fase 1: Setup Inicial (1-2 días)
- ✅ Crear estructura de solución .NET
- ✅ Configurar proyectos base
- ✅ Configurar Docker
- ✅ Configurar CI/CD básico

### Fase 2: Products Service (2-3 días)
- ✅ Migrar entidades y DTOs
- ✅ Implementar repositorio con EF Core
- ✅ Implementar servicio y controlador
- ✅ Configurar PostgreSQL
- ✅ Swagger
- ✅ Tests unitarios

### Fase 3: Inventory Service (1-2 días)
- ✅ Migrar entidades
- ✅ Implementar lógica de actualización de stock
- ✅ Configurar PostgreSQL
- ✅ Integración con Kafka (producer)

### Fase 4: Orders Service (3-4 días)
- ✅ Migrar entidades (Order, OrderItems)
- ✅ Implementar relación 1:N en EF Core
- ✅ Configurar MySQL
- ✅ Implementar HTTP Client para Inventory
- ✅ Implementar Kafka producer
- ✅ Lógica de creación de órdenes

### Fase 5: Notifications Service (2-3 días)
- ✅ Implementar Kafka consumer
- ✅ Configurar MailKit
- ✅ Implementar envío de emails
- ✅ Persistencia en H2 (o PostgreSQL)

### Fase 6: API Gateway (1-2 días)
- ✅ Configurar Ocelot
- ✅ Configurar rutas
- ✅ Integrar con Eureka
- ✅ Circuit Breaker (QoS)
- ✅ CORS

### Fase 7: Integración y Testing (2-3 días)
- ✅ Integración completa de todos los servicios
- ✅ Testing end-to-end
- ✅ Verificar flujo completo de compra
- ✅ Verificar eventos Kafka
- ✅ Verificar envío de emails

### Fase 8: Documentación (1 día)
- ✅ README actualizado
- ✅ Guías de despliegue
- ✅ Arquitectura en .NET
- ✅ Comparativa Java vs .NET

---

## 12. Comandos Útiles

### Crear Solución

```bash
# Crear solución
dotnet new sln -n Linktic

# Crear proyectos
dotnet new webapi -n Linktic.Products.Api -o src/Services/Linktic.Products/Linktic.Products.Api
dotnet new classlib -n Linktic.Products.Domain -o src/Services/Linktic.Products/Linktic.Products.Domain
dotnet new classlib -n Linktic.Products.Application -o src/Services/Linktic.Products/Linktic.Products.Application
dotnet new classlib -n Linktic.Products.Infrastructure -o src/Services/Linktic.Products/Linktic.Products.Infrastructure

# Agregar a solución
dotnet sln add src/Services/Linktic.Products/Linktic.Products.Api
dotnet sln add src/Services/Linktic.Products/Linktic.Products.Domain
dotnet sln add src/Services/Linktic.Products/Linktic.Products.Application
dotnet sln add src/Services/Linktic.Products/Linktic.Products.Infrastructure

# Referencias entre proyectos
cd src/Services/Linktic.Products/Linktic.Products.Api
dotnet add reference ../Linktic.Products.Application
dotnet add reference ../Linktic.Products.Infrastructure

cd ../Linktic.Products.Application
dotnet add reference ../Linktic.Products.Domain

cd ../Linktic.Products.Infrastructure
dotnet add reference ../Linktic.Products.Domain
```

### Ejecutar Servicios

```bash
# Products Service
cd src/Services/Linktic.Products/Linktic.Products.Api
dotnet run

# Docker Compose
docker-compose up --build
```

---

## 13. Recomendaciones

1. **Usar .NET 8 (LTS)** - Soporte hasta noviembre 2026
2. **Entity Framework Core 8** - Última versión estable
3. **Ocelot** para API Gateway (más maduro que YARP para Eureka)
4. **MailKit** en lugar de SmtpClient (obsoleto)
5. **Serilog** para logging estructurado
6. **FluentValidation** para validaciones complejas
7. **Polly** para Circuit Breaker y Retry policies
8. **xUnit** para tests (más usado en .NET)
9. **Usar Minimal APIs** si se desea código más conciso (opcional)
10. **Health Checks** con Microsoft.Extensions.Diagnostics.HealthChecks

---

## 14. Puntos de Atención

⚠️ **Diferencias sutiles**:
- **BigDecimal (Java) → decimal (C#)**: Precisión similar pero sintaxis diferente
- **LocalDateTime → DateTime**: Usar DateTime.UtcNow en lugar de DateTime.Now
- **@Transactional → DbContext.SaveChangesAsync()**: Transacciones implícitas
- **Lombok → Records o propiedades automáticas**: C# tiene menos boilerplate nativo
- **Streams (Java) → LINQ (C#)**: Sintaxis diferente pero concepto similar

⚠️ **Configuración de Eureka**:
- Steeltoe Eureka Client funciona bien
- Mantener Eureka Server en Java es más estable
- Alternativa: Consul (nativo .NET)

⚠️ **Kafka en .NET**:
- Confluent.Kafka es la biblioteca oficial
- MassTransit es alternativa más high-level (opcional)

---

## 15. Frontend Angular - Sin Cambios

El frontend Angular **NO requiere cambios** si:
1. Los endpoints REST mantienen la misma estructura
2. Los DTOs tienen los mismos campos
3. El API Gateway mantiene el mismo puerto (8080)

**Única modificación necesaria**:
- Cambiar `baseUrl` si es necesario (pero debería ser la misma)

---

## 16. Conclusión

Esta migración de **Java Spring Boot → .NET 8** mantiene:
- ✅ Arquitectura de microservicios idéntica
- ✅ Mismos endpoints REST
- ✅ Mismas bases de datos
- ✅ Mismo frontend Angular
- ✅ Mismos patrones de diseño
- ✅ Misma comunicación asíncrona (Kafka)
- ✅ Mismo Service Discovery (Eureka)

**Ventajas de .NET 8**:
- 🚀 Mejor rendimiento (benchmarks)
- 💰 Menor consumo de memoria
- 🔧 Menos boilerplate (no necesita Lombok)
- 📦 NuGet Package Manager
- 🐳 Imágenes Docker más ligeras
- 🆓 Completamente gratis y open-source

**Tiempo estimado de migración**: 10-15 días de desarrollo activo

---

## 17. Próximos Pasos

1. **Revisar y aprobar este plan**
2. **Crear estructura base del proyecto .NET**
3. **Comenzar con Products Service** (más simple)
4. **Migrar incrementalmente** cada microservicio
5. **Testing continuo** después de cada migración
6. **Documentar diferencias** encontradas durante el proceso

---

¿Deseas que proceda con la creación de la estructura base del proyecto .NET?
