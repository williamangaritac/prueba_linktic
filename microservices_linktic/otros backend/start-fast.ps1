# Script ULTRA RÁPIDO para levantar todo SIN Docker
# Usa los JARs ya compilados - Inicia en 30-60 segundos

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  🚀 INICIO RÁPIDO (SIN DOCKER)" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$basePath = "C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic"
$frontendPath = "C:\Users\willi\OneDrive\Escritorio\prueba_linktic\frontend_angular"

# Detener servicios existentes
Write-Host "🛑 Deteniendo servicios existentes..." -ForegroundColor Yellow
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Stop-Process -Name node -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2
Write-Host "✅ Servicios detenidos`n" -ForegroundColor Green

# Verificar que los JARs existen
Write-Host "📦 Verificando archivos JAR..." -ForegroundColor Cyan
$jars = @(
    "eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar",
    "products_service\target\products_service-0.0.1-SNAPSHOT.jar",
    "inventory_service\target\inventory_service-0.0.1-SNAPSHOT.jar",
    "orders_service\target\orders_service-0.0.1-SNAPSHOT.jar",
    "notifications_service\target\notifications_service-0.0.1-SNAPSHOT.jar"
)

$allExist = $true
foreach ($jar in $jars) {
    if (Test-Path $jar) {
        Write-Host "  ✅ $jar" -ForegroundColor Green
    } else {
        Write-Host "  ❌ $jar - NO EXISTE" -ForegroundColor Red
        $allExist = $false
    }
}

if (-not $allExist) {
    Write-Host "`n❌ Faltan archivos JAR. Ejecuta primero:" -ForegroundColor Red
    Write-Host "   mvnw clean package -DskipTests`n" -ForegroundColor Yellow
    exit 1
}

Write-Host "`n✅ Todos los JARs encontrados`n" -ForegroundColor Green

# Iniciar servicios en background
Write-Host "🚀 Iniciando servicios en background...`n" -ForegroundColor Cyan

# 1. Eureka Server
Write-Host "1️⃣  Eureka Server (puerto 8761)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🟢 EUREKA SERVER' -ForegroundColor Green; java -jar eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 15

# 2. Products Service
Write-Host "2️⃣  Products Service (puerto 8081)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🟢 PRODUCTS SERVICE' -ForegroundColor Green; java -jar products_service\target\products_service-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 8

# 3. Inventory Service
Write-Host "3️⃣  Inventory Service (puerto 8082)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🟢 INVENTORY SERVICE' -ForegroundColor Green; java -jar inventory_service\target\inventory_service-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 8

# 4. Orders Service
Write-Host "4️⃣  Orders Service (puerto 8083)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🟢 ORDERS SERVICE' -ForegroundColor Green; java -jar orders_service\target\orders_service-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 8

# 5. Notifications Service
Write-Host "5️⃣  Notifications Service (puerto 8084)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🟢 NOTIFICATIONS SERVICE' -ForegroundColor Green; java -jar notifications_service\target\notifications_service-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 5

# 6. Frontend Angular
Write-Host "6️⃣  Frontend Angular (puerto 4200)..." -ForegroundColor Cyan
if (Test-Path $frontendPath) {
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendPath'; Write-Host '🟢 FRONTEND ANGULAR' -ForegroundColor Green; npm start" -WindowStyle Minimized
} else {
    Write-Host "  ⚠️  Frontend no encontrado en: $frontendPath" -ForegroundColor Yellow
}

Write-Host "`n✅ Todos los servicios iniciados en ventanas minimizadas" -ForegroundColor Green
Write-Host "⏳ Esperando 30 segundos para que terminen de iniciar...`n" -ForegroundColor Yellow

Start-Sleep -Seconds 30

# Verificar puertos
Write-Host "📊 Verificando puertos...`n" -ForegroundColor Cyan

$services = @(
    @{Name="Eureka Server"; Port=8761},
    @{Name="Products Service"; Port=8081},
    @{Name="Inventory Service"; Port=8082},
    @{Name="Orders Service"; Port=8083},
    @{Name="Notifications Service"; Port=8084},
    @{Name="Frontend Angular"; Port=4200}
)

$activeCount = 0
foreach ($service in $services) {
    try {
        $connection = Test-NetConnection -ComputerName localhost -Port $service.Port -WarningAction SilentlyContinue -InformationLevel Quiet -ErrorAction Stop
        if ($connection) {
            Write-Host "✅ $($service.Name) (Puerto $($service.Port)) - ACTIVO" -ForegroundColor Green
            $activeCount++
        } else {
            Write-Host "⏳ $($service.Name) (Puerto $($service.Port)) - INICIANDO..." -ForegroundColor Yellow
        }
    } catch {
        Write-Host "⏳ $($service.Name) (Puerto $($service.Port)) - INICIANDO..." -ForegroundColor Yellow
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  ✅ DESPLIEGUE COMPLETADO" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "📊 Servicios activos: $activeCount de 6`n" -ForegroundColor Cyan

Write-Host "🌐 URLs de acceso:" -ForegroundColor Yellow
Write-Host "   • Frontend: http://localhost:4200" -ForegroundColor White
Write-Host "   • Eureka: http://localhost:8761" -ForegroundColor White
Write-Host "   • Products API: http://localhost:8081/api/v1/frontend/products?page=0`n" -ForegroundColor White

Write-Host "💡 Las ventanas están minimizadas. Para verlas:" -ForegroundColor Cyan
Write-Host "   - Busca los iconos de PowerShell en la barra de tareas" -ForegroundColor White
Write-Host "   - Haz click para ver los logs de cada servicio`n" -ForegroundColor White

Write-Host "🛑 Para detener todos los servicios:" -ForegroundColor Yellow
Write-Host "   Stop-Process -Name java -Force" -ForegroundColor White
Write-Host "   Stop-Process -Name node -Force`n" -ForegroundColor White

Write-Host "⚡ TIEMPO TOTAL: ~60 segundos (vs 3-5 minutos con Docker)`n" -ForegroundColor Green

Write-Host "========================================`n" -ForegroundColor Cyan

# Abrir navegador automáticamente
Write-Host "🌐 Abriendo navegador en 5 segundos..." -ForegroundColor Cyan
Start-Sleep -Seconds 5
Start-Process "http://localhost:4200"

