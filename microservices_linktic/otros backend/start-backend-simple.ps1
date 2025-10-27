# Script simple para iniciar todos los servicios del backend

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  INICIANDO BACKEND" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

# Detener servicios existentes
Write-Host "🛑 Deteniendo servicios existentes..." -ForegroundColor Yellow
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$basePath = "C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic"

# Iniciar Eureka Server
Write-Host "1️⃣  Iniciando Eureka Server..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🚀 EUREKA SERVER - Puerto 8761' -ForegroundColor Green; java -jar eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar"
) -WindowStyle Normal

Start-Sleep -Seconds 20

# Iniciar Products Service
Write-Host "2️⃣  Iniciando Products Service..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🚀 PRODUCTS SERVICE - Puerto 8081' -ForegroundColor Green; java -jar products_service\target\products_service-0.0.1-SNAPSHOT.jar"
) -WindowStyle Normal

Start-Sleep -Seconds 10

# Iniciar Inventory Service
Write-Host "3️⃣  Iniciando Inventory Service..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🚀 INVENTORY SERVICE - Puerto 8082' -ForegroundColor Green; java -jar inventory_service\target\inventory_service-0.0.1-SNAPSHOT.jar"
) -WindowStyle Normal

Start-Sleep -Seconds 10

# Iniciar Orders Service
Write-Host "4️⃣  Iniciando Orders Service..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🚀 ORDERS SERVICE - Puerto 8083' -ForegroundColor Green; java -jar orders_service\target\orders_service-0.0.1-SNAPSHOT.jar"
) -WindowStyle Normal

Start-Sleep -Seconds 10

# Iniciar Notifications Service
Write-Host "5️⃣  Iniciando Notifications Service..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$basePath'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'; Write-Host '🚀 NOTIFICATIONS SERVICE - Puerto 8084' -ForegroundColor Green; java -jar notifications_service\target\notifications_service-0.0.1-SNAPSHOT.jar"
) -WindowStyle Normal

Write-Host "`n✅ Todos los servicios iniciados en ventanas separadas" -ForegroundColor Green
Write-Host "⏳ Esperando 40 segundos para que terminen de iniciar...`n" -ForegroundColor Yellow

Start-Sleep -Seconds 40

Write-Host "📊 Verificando estado de los servicios...`n" -ForegroundColor Cyan

$services = @(
    @{Name="Eureka Server"; Port=8761},
    @{Name="Products Service"; Port=8081},
    @{Name="Inventory Service"; Port=8082},
    @{Name="Orders Service"; Port=8083},
    @{Name="Notifications Service"; Port=8084}
)

foreach ($service in $services) {
    try {
        $connection = Test-NetConnection -ComputerName localhost -Port $service.Port -WarningAction SilentlyContinue -InformationLevel Quiet -ErrorAction Stop
        if ($connection) {
            Write-Host "✅ $($service.Name) (Puerto $($service.Port)) - ACTIVO" -ForegroundColor Green
        } else {
            Write-Host "❌ $($service.Name) (Puerto $($service.Port)) - INACTIVO" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ $($service.Name) (Puerto $($service.Port)) - INACTIVO" -ForegroundColor Red
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  BACKEND DESPLEGADO" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "📝 URLs importantes:" -ForegroundColor Yellow
Write-Host "   • Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "   • Products API: http://localhost:8081/api/v1/frontend/products?page=0" -ForegroundColor White
Write-Host "   • Orders API: http://localhost:8083/api/v1/frontend/orders/purchase`n" -ForegroundColor White

Write-Host "💡 Las ventanas de PowerShell muestran los logs de cada servicio" -ForegroundColor Cyan
Write-Host "💡 Para detener: Stop-Process -Name java -Force`n" -ForegroundColor Cyan

