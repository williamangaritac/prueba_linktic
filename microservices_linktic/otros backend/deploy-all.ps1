# Script para desplegar todos los microservicios

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  DESPLEGANDO TODOS LOS SERVICIOS" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"

# Detener servicios existentes
Write-Host "🛑 Deteniendo servicios existentes..." -ForegroundColor Yellow
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Stop-Process -Name node -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

# Iniciar Eureka Server
Write-Host "`n1️⃣  Iniciando Eureka Server (puerto 8761)..." -ForegroundColor Cyan
Start-Job -Name "EurekaServer" -ScriptBlock {
    $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
    Set-Location "C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic"
    & java -jar eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar
} | Out-Null

Start-Sleep -Seconds 15

# Iniciar Products Service
Write-Host "2️⃣  Iniciando Products Service (puerto 8081)..." -ForegroundColor Cyan
Start-Job -Name "ProductsService" -ScriptBlock {
    $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
    Set-Location "C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic"
    & java -jar products_service\target\products_service-0.0.1-SNAPSHOT.jar
} | Out-Null

Start-Sleep -Seconds 5

# Iniciar Inventory Service
Write-Host "3️⃣  Iniciando Inventory Service (puerto 8082)..." -ForegroundColor Cyan
Start-Job -Name "InventoryService" -ScriptBlock {
    $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
    Set-Location "C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic"
    & java -jar inventory_service\target\inventory_service-0.0.1-SNAPSHOT.jar
} | Out-Null

Start-Sleep -Seconds 5

# Iniciar Orders Service
Write-Host "4️⃣  Iniciando Orders Service (puerto 8083)..." -ForegroundColor Cyan
Start-Job -Name "OrdersService" -ScriptBlock {
    $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
    Set-Location "C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic"
    & java -jar orders_service\target\orders_service-0.0.1-SNAPSHOT.jar
} | Out-Null

Start-Sleep -Seconds 5

# Iniciar Notifications Service
Write-Host "5️⃣  Iniciando Notifications Service (puerto 8084)..." -ForegroundColor Cyan
Start-Job -Name "NotificationsService" -ScriptBlock {
    $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
    Set-Location "C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic"
    & java -jar notifications_service\target\notifications_service-0.0.1-SNAPSHOT.jar
} | Out-Null

Write-Host "`n✅ Todos los servicios iniciados en background" -ForegroundColor Green
Write-Host "⏳ Esperando 45 segundos para que todos los servicios terminen de iniciar...`n" -ForegroundColor Yellow

Start-Sleep -Seconds 45

Write-Host "📊 Estado de los Jobs:" -ForegroundColor Cyan
Get-Job | Format-Table -AutoSize

Write-Host "`n📊 Verificando puertos...`n" -ForegroundColor Cyan

$ports = @(8761, 8081, 8082, 8083, 8084)
foreach ($port in $ports) {
    $connection = Test-NetConnection -ComputerName localhost -Port $port -WarningAction SilentlyContinue -InformationLevel Quiet
    if ($connection) {
        Write-Host "✅ Puerto $port - ACTIVO" -ForegroundColor Green
    } else {
        Write-Host "❌ Puerto $port - INACTIVO" -ForegroundColor Red
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  BACKEND DESPLEGADO" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "📝 URLs de los servicios:" -ForegroundColor Yellow
Write-Host "   • Eureka Server: http://localhost:8761" -ForegroundColor White
Write-Host "   • Products Service: http://localhost:8081/api/v1/frontend/products?page=0" -ForegroundColor White
Write-Host "   • Inventory Service: http://localhost:8082" -ForegroundColor White
Write-Host "   • Orders Service: http://localhost:8083" -ForegroundColor White
Write-Host "   • Notifications Service: http://localhost:8084`n" -ForegroundColor White

Write-Host "💡 Para ver los logs de un servicio:" -ForegroundColor Cyan
Write-Host "   Receive-Job -Name EurekaServer -Keep" -ForegroundColor White
Write-Host "   Receive-Job -Name ProductsService -Keep" -ForegroundColor White
Write-Host "   Receive-Job -Name InventoryService -Keep" -ForegroundColor White
Write-Host "   Receive-Job -Name OrdersService -Keep" -ForegroundColor White
Write-Host "   Receive-Job -Name NotificationsService -Keep`n" -ForegroundColor White

Write-Host "💡 Para detener todos los servicios:" -ForegroundColor Cyan
Write-Host "   Get-Job | Stop-Job; Get-Job | Remove-Job; Stop-Process -Name java -Force`n" -ForegroundColor White

