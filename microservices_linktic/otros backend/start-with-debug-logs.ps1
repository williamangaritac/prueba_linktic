# Script para iniciar todos los microservicios con logs DEBUG (muy detallados)
# Muestra información completa de cada proceso interno

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  INICIANDO CON LOGS DEBUG" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "⚠️  MODO DEBUG ACTIVADO" -ForegroundColor Red
Write-Host "📊 Verás información MUY detallada de cada proceso:" -ForegroundColor Yellow
Write-Host "   • Consultas SQL completas" -ForegroundColor White
Write-Host "   • Peticiones HTTP con headers" -ForegroundColor White
Write-Host "   • Eventos de Kafka" -ForegroundColor White
Write-Host "   • Registro en Eureka" -ForegroundColor White
Write-Host "   • Transacciones de base de datos" -ForegroundColor White
Write-Host "   • Y mucho más...`n" -ForegroundColor White

# Función para iniciar servicio en nueva ventana con DEBUG
function Start-ServiceDebugWindow {
    param(
        [string]$ServiceName,
        [string]$Port,
        [string]$JarPath,
        [string]$Color = "Green"
    )
    
    $title = "$ServiceName - Puerto $Port - DEBUG MODE"
    $command = @"
`$Host.UI.RawUI.WindowTitle = '$title'
Write-Host '========================================' -ForegroundColor $Color
Write-Host '  $ServiceName - DEBUG MODE' -ForegroundColor Yellow
Write-Host '  Puerto: $Port' -ForegroundColor White
Write-Host '========================================' -ForegroundColor $Color
Write-Host ''
Write-Host '📊 Logs DEBUG activados - Información muy detallada' -ForegroundColor Yellow
Write-Host ''
`$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'
`$env:PATH = "`$env:JAVA_HOME\bin;`$env:PATH"
java -jar '$JarPath' ``
  --logging.level.root=INFO ``
  --logging.level.com.linktic_test=DEBUG ``
  --logging.level.org.springframework.web=DEBUG ``
  --logging.level.org.springframework.kafka=DEBUG ``
  --logging.level.org.springframework.mail=DEBUG ``
  --logging.level.org.hibernate.SQL=DEBUG ``
  --logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE ``
  --logging.pattern.console='%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n'
"@
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
    Write-Host "✅ $ServiceName iniciado en nueva ventana (Puerto $Port) - DEBUG MODE" -ForegroundColor Green
}

# 1. Iniciar Eureka Server
Write-Host "1️⃣  Iniciando Eureka Server (DEBUG)..." -ForegroundColor Cyan
Start-ServiceDebugWindow -ServiceName "EUREKA SERVER" -Port "8761" -JarPath "$PWD\eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar" -Color "Magenta"
Start-Sleep -Seconds 2

Write-Host "`n⏳ Esperando 25 segundos para que Eureka Server inicie...`n" -ForegroundColor Yellow
Start-Sleep -Seconds 25

# 2. Iniciar Products Service
Write-Host "2️⃣  Iniciando Products Service (DEBUG)..." -ForegroundColor Cyan
Start-ServiceDebugWindow -ServiceName "PRODUCTS SERVICE" -Port "8081" -JarPath "$PWD\products_service\target\products_service-0.0.1-SNAPSHOT.jar" -Color "Green"
Start-Sleep -Seconds 2

# 3. Iniciar Inventory Service
Write-Host "3️⃣  Iniciando Inventory Service (DEBUG)..." -ForegroundColor Cyan
Start-ServiceDebugWindow -ServiceName "INVENTORY SERVICE" -Port "8082" -JarPath "$PWD\inventory_service\target\inventory_service-0.0.1-SNAPSHOT.jar" -Color "Blue"
Start-Sleep -Seconds 2

# 4. Iniciar Orders Service
Write-Host "4️⃣  Iniciando Orders Service (DEBUG)..." -ForegroundColor Cyan
Start-ServiceDebugWindow -ServiceName "ORDERS SERVICE" -Port "8083" -JarPath "$PWD\orders_service\target\orders_service-0.0.1-SNAPSHOT.jar" -Color "Yellow"
Start-Sleep -Seconds 2

# 5. Iniciar Notifications Service
Write-Host "5️⃣  Iniciando Notifications Service (DEBUG)..." -ForegroundColor Cyan
Start-ServiceDebugWindow -ServiceName "NOTIFICATIONS SERVICE" -Port "8084" -JarPath "$PWD\notifications_service\target\notifications_service-0.0.1-SNAPSHOT.jar" -Color "Cyan"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  ✅ TODOS LOS SERVICIOS EN DEBUG MODE" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "📊 Información que verás en los logs:" -ForegroundColor Yellow
Write-Host ""
Write-Host "🔍 PRODUCTS SERVICE:" -ForegroundColor Green
Write-Host "   • Consultas SQL a PostgreSQL (SELECT, INSERT, UPDATE)" -ForegroundColor White
Write-Host "   • Peticiones HTTP GET/POST con parámetros" -ForegroundColor White
Write-Host "   • Registro en Eureka cada 10 segundos" -ForegroundColor White
Write-Host "   • Mapeo de entidades a DTOs" -ForegroundColor White

Write-Host "`n🔍 INVENTORY SERVICE:" -ForegroundColor Blue
Write-Host "   • Validación de stock disponible" -ForegroundColor White
Write-Host "   • Actualización de inventario" -ForegroundColor White
Write-Host "   • Transacciones de base de datos" -ForegroundColor White

Write-Host "`n🔍 ORDERS SERVICE:" -ForegroundColor Yellow
Write-Host "   • Creación de órdenes en MySQL" -ForegroundColor White
Write-Host "   • Llamadas a Products Service (RestTemplate)" -ForegroundColor White
Write-Host "   • Llamadas a Inventory Service" -ForegroundColor White
Write-Host "   • Publicación de eventos en Kafka" -ForegroundColor White
Write-Host "   • Generación de número de orden" -ForegroundColor White

Write-Host "`n🔍 NOTIFICATIONS SERVICE:" -ForegroundColor Cyan
Write-Host "   • Consumo de eventos de Kafka" -ForegroundColor White
Write-Host "   • Preparación de emails" -ForegroundColor White
Write-Host "   • Conexión SMTP (si está configurado)" -ForegroundColor White
Write-Host "   • Guardado de notificaciones en PostgreSQL" -ForegroundColor White

Write-Host "`n💡 Ejemplo de flujo completo al crear una orden:" -ForegroundColor Yellow
Write-Host "   1. Frontend → POST /api/v1/frontend/orders/purchase" -ForegroundColor White
Write-Host "   2. Orders Service → GET products_service/products/sku/{sku}" -ForegroundColor White
Write-Host "   3. Orders Service → POST inventory_service/validate" -ForegroundColor White
Write-Host "   4. Orders Service → INSERT INTO orders (MySQL)" -ForegroundColor White
Write-Host "   5. Orders Service → Kafka.send('order-events', event)" -ForegroundColor White
Write-Host "   6. Notifications Service → Kafka.consume('order-events')" -ForegroundColor White
Write-Host "   7. Notifications Service → SMTP.send(email)" -ForegroundColor White
Write-Host "   8. Notifications Service → INSERT INTO notifications (PostgreSQL)" -ForegroundColor White

Write-Host "`n⏳ Esperando 30 segundos para que todos los servicios inicien...`n" -ForegroundColor Cyan
Start-Sleep -Seconds 30

Write-Host "🔍 Verificando estado...`n" -ForegroundColor Cyan
.\verificar-servicios.ps1

Write-Host "`n🧪 Para ver el flujo completo en acción:" -ForegroundColor Yellow
Write-Host "   1. Observa las ventanas de logs" -ForegroundColor White
Write-Host "   2. Ejecuta: .\probar-endpoints.ps1" -ForegroundColor White
Write-Host "   3. Verás en tiempo real cómo cada servicio procesa la petición" -ForegroundColor White

Write-Host "`n========================================`n" -ForegroundColor Cyan

