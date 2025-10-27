# Script para agregar configuración de Eureka a los microservicios
# Este script agrega la configuración de Eureka Client a los archivos application.yml

$services = @("inventory_service", "orders_service", "notifications_service")

$eurekaConfig = @"

# Eureka Client Configuration
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    instance-id: `${spring.application.name}:`${server.port}
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
"@

foreach ($service in $services) {
    $configFile = ".\$service\src\main\resources\application.yml"
    
    if (Test-Path $configFile) {
        Write-Host "Actualizando $configFile..." -ForegroundColor Cyan
        
        # Leer el contenido actual
        $content = Get-Content $configFile -Raw
        
        # Verificar si ya tiene configuración de Eureka
        if ($content -notmatch "eureka:") {
            # Agregar configuración de Eureka al final
            Add-Content -Path $configFile -Value $eurekaConfig
            Write-Host "✅ Configuración de Eureka agregada a $service" -ForegroundColor Green
        } else {
            Write-Host "⚠️  $service ya tiene configuración de Eureka" -ForegroundColor Yellow
        }
    } else {
        Write-Host "❌ No se encontró $configFile" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Configuración completada" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

