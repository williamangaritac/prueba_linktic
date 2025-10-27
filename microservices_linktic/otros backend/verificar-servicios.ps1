# Script para verificar el estado de todos los microservicios

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  VERIFICANDO ESTADO DE MICROSERVICIOS" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

# Definir servicios
$services = @(
    @{Port=8761; Name="Eureka Server"; URL="http://localhost:8761"},
    @{Port=8081; Name="Products Service"; URL="http://localhost:8081/api/v1/actuator/health"},
    @{Port=8082; Name="Inventory Service"; URL="http://localhost:8082/api/v1/actuator/health"},
    @{Port=8083; Name="Orders Service"; URL="http://localhost:8083/api/v1/actuator/health"},
    @{Port=8084; Name="Notifications Service"; URL="http://localhost:8084/api/v1/actuator/health"}
)

# Verificar puertos
Write-Host "📊 Estado de Puertos:" -ForegroundColor Cyan
Write-Host "─────────────────────────────────────────" -ForegroundColor DarkGray

foreach ($service in $services) {
    $port = $service.Port
    $name = $service.Name
    $listening = netstat -an | Select-String ":$port " | Select-String "LISTENING"
    
    if ($listening) {
        Write-Host "✅ $name" -NoNewline -ForegroundColor Green
        Write-Host " (Puerto $port) - " -NoNewline
        Write-Host "ACTIVO" -ForegroundColor Green
    } else {
        Write-Host "❌ $name" -NoNewline -ForegroundColor Red
        Write-Host " (Puerto $port) - " -NoNewline
        Write-Host "INACTIVO" -ForegroundColor Red
    }
}

# Verificar bases de datos
Write-Host "`n📊 Estado de Bases de Datos:" -ForegroundColor Cyan
Write-Host "─────────────────────────────────────────" -ForegroundColor DarkGray

$postgres = Get-Service -Name "postgresql-x64-15" -ErrorAction SilentlyContinue
if ($postgres -and $postgres.Status -eq "Running") {
    Write-Host "✅ PostgreSQL - ACTIVO" -ForegroundColor Green
} else {
    Write-Host "❌ PostgreSQL - INACTIVO" -ForegroundColor Red
}

$mysql = Get-Service -Name "mysql" -ErrorAction SilentlyContinue
if ($mysql -and $mysql.Status -eq "Running") {
    Write-Host "✅ MySQL - ACTIVO" -ForegroundColor Green
} else {
    Write-Host "❌ MySQL - INACTIVO" -ForegroundColor Red
}

# Verificar procesos Java
Write-Host "`n📊 Procesos Java Activos:" -ForegroundColor Cyan
Write-Host "─────────────────────────────────────────" -ForegroundColor DarkGray

$javaProcesses = Get-Process java -ErrorAction SilentlyContinue
if ($javaProcesses) {
    Write-Host "Total de procesos Java: $($javaProcesses.Count)" -ForegroundColor Yellow
    $javaProcesses | Select-Object Id, @{Name='Memory (MB)';Expression={[math]::Round($_.WorkingSet64/1MB,2)}}, StartTime | Format-Table -AutoSize
} else {
    Write-Host "No hay procesos Java corriendo" -ForegroundColor Red
}

# Resumen
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  RESUMEN" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

$activeServices = 0
foreach ($service in $services) {
    $port = $service.Port
    $listening = netstat -an | Select-String ":$port " | Select-String "LISTENING"
    if ($listening) { $activeServices++ }
}

Write-Host "Servicios activos: $activeServices de $($services.Count)" -ForegroundColor $(if ($activeServices -eq $services.Count) { "Green" } else { "Yellow" })

if ($activeServices -eq $services.Count) {
    Write-Host "`n✅ ¡Todos los servicios están corriendo correctamente!" -ForegroundColor Green
    Write-Host "`n📍 URLs Importantes:" -ForegroundColor Cyan
    Write-Host "   • Eureka Dashboard: http://localhost:8761" -ForegroundColor White
    Write-Host "   • Products Swagger: http://localhost:8081/api/v1/swagger-ui.html" -ForegroundColor White
    Write-Host "   • Inventory Swagger: http://localhost:8082/api/v1/swagger-ui.html" -ForegroundColor White
    Write-Host "   • Orders Swagger: http://localhost:8083/api/v1/swagger-ui.html" -ForegroundColor White
    Write-Host "   • Notifications Swagger: http://localhost:8084/api/v1/swagger-ui.html" -ForegroundColor White
} else {
    Write-Host "`n⚠️  Algunos servicios no están corriendo" -ForegroundColor Yellow
    Write-Host "   Revisa los logs de los servicios inactivos" -ForegroundColor Yellow
}

Write-Host "`n========================================`n" -ForegroundColor Cyan

