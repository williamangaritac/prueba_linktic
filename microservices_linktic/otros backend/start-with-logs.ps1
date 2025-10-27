# Script para iniciar todos los microservicios con logs visibles en consola
# Cada servicio se abre en una nueva ventana de PowerShell

param(
    [switch]$Debug = $false
)

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  INICIANDO MICROSERVICIOS CON LOGS" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

# Configurar nivel de logs
$logLevel = if ($Debug) { "DEBUG" } else { "INFO" }

Write-Host "📊 Nivel de logs: $logLevel" -ForegroundColor Cyan
Write-Host "💡 Cada servicio se abrirá en una ventana separada`n" -ForegroundColor Yellow

# Función para iniciar servicio en nueva ventana
function Start-ServiceWindow {
    param(
        [string]$ServiceName,
        [string]$Port,
        [string]$JarPath,
        [string]$Color = "Green"
    )
    
    $title = "$ServiceName - Puerto $Port"
    $command = @"
`$Host.UI.RawUI.WindowTitle = '$title'
Write-Host '========================================' -ForegroundColor $Color
Write-Host '  $ServiceName' -ForegroundColor Yellow
Write-Host '  Puerto: $Port' -ForegroundColor White
Write-Host '========================================' -ForegroundColor $Color
Write-Host ''
`$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'
`$env:PATH = "`$env:JAVA_HOME\bin;`$env:PATH"
java -jar '$JarPath' --logging.level.root=$logLevel --logging.level.com.linktic_test=$logLevel
"@
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
    Write-Host "✅ $ServiceName iniciado en nueva ventana (Puerto $Port)" -ForegroundColor Green
}

# 1. Iniciar Eureka Server
Write-Host "1️⃣  Iniciando Eureka Server..." -ForegroundColor Cyan
Start-ServiceWindow -ServiceName "EUREKA SERVER" -Port "8761" -JarPath "$PWD\eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar" -Color "Magenta"
Start-Sleep -Seconds 2

Write-Host "`n⏳ Esperando 25 segundos para que Eureka Server inicie...`n" -ForegroundColor Yellow
Start-Sleep -Seconds 25

# 2. Iniciar Products Service
Write-Host "2️⃣  Iniciando Products Service..." -ForegroundColor Cyan
Start-ServiceWindow -ServiceName "PRODUCTS SERVICE" -Port "8081" -JarPath "$PWD\products_service\target\products_service-0.0.1-SNAPSHOT.jar" -Color "Green"
Start-Sleep -Seconds 2

# 3. Iniciar Inventory Service
Write-Host "3️⃣  Iniciando Inventory Service..." -ForegroundColor Cyan
Start-ServiceWindow -ServiceName "INVENTORY SERVICE" -Port "8082" -JarPath "$PWD\inventory_service\target\inventory_service-0.0.1-SNAPSHOT.jar" -Color "Blue"
Start-Sleep -Seconds 2

# 4. Iniciar Orders Service
Write-Host "4️⃣  Iniciando Orders Service..." -ForegroundColor Cyan
Start-ServiceWindow -ServiceName "ORDERS SERVICE" -Port "8083" -JarPath "$PWD\orders_service\target\orders_service-0.0.1-SNAPSHOT.jar" -Color "Yellow"
Start-Sleep -Seconds 2

# 5. Iniciar Notifications Service
Write-Host "5️⃣  Iniciando Notifications Service..." -ForegroundColor Cyan
Start-ServiceWindow -ServiceName "NOTIFICATIONS SERVICE" -Port "8084" -JarPath "$PWD\notifications_service\target\notifications_service-0.0.1-SNAPSHOT.jar" -Color "Cyan"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  ✅ TODOS LOS SERVICIOS INICIADOS" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "📊 Se han abierto 5 ventanas de PowerShell:" -ForegroundColor Yellow
Write-Host "   1. Eureka Server (8761) - Magenta" -ForegroundColor Magenta
Write-Host "   2. Products Service (8081) - Verde" -ForegroundColor Green
Write-Host "   3. Inventory Service (8082) - Azul" -ForegroundColor Blue
Write-Host "   4. Orders Service (8083) - Amarillo" -ForegroundColor Yellow
Write-Host "   5. Notifications Service (8084) - Cyan" -ForegroundColor Cyan

Write-Host "`n💡 Consejos:" -ForegroundColor Yellow
Write-Host "   • Cada ventana muestra los logs en tiempo real" -ForegroundColor White
Write-Host "   • Puedes ver cómo cada servicio procesa las peticiones" -ForegroundColor White
Write-Host "   • Para detener un servicio: Ctrl+C en su ventana" -ForegroundColor White
Write-Host "   • Para detener todos: Ejecuta 'Stop-Process -Name java -Force'" -ForegroundColor White

Write-Host "`n⏳ Esperando 30 segundos para que todos los servicios inicien...`n" -ForegroundColor Cyan
Start-Sleep -Seconds 30

Write-Host "🔍 Verificando estado de los servicios...`n" -ForegroundColor Cyan
.\verificar-servicios.ps1

Write-Host "`n📍 URLs Importantes:" -ForegroundColor Yellow
Write-Host "   • Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "   • Products Swagger: http://localhost:8081/api/v1/swagger-ui.html" -ForegroundColor White
Write-Host "   • Orders Swagger: http://localhost:8083/api/v1/swagger-ui.html" -ForegroundColor White

Write-Host "`n🧪 Para probar los endpoints:" -ForegroundColor Yellow
Write-Host "   .\probar-endpoints.ps1" -ForegroundColor White

Write-Host "`n========================================`n" -ForegroundColor Cyan

