# Script para iniciar servicios y mostrar logs consolidados
# Muestra logs de todos los servicios en una sola consola

param(
    [string]$Service = "all"
)

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  LOGS CONSOLIDADOS DE MICROSERVICIOS" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

if ($Service -eq "eureka") {
    Write-Host "🔍 Iniciando EUREKA SERVER con logs detallados...`n" -ForegroundColor Magenta
    java -jar eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar `
        --logging.level.root=INFO `
        --logging.level.com.netflix.eureka=DEBUG `
        --logging.level.com.netflix.discovery=DEBUG `
        --logging.pattern.console="%d{HH:mm:ss.SSS} [EUREKA] [%thread] %-5level %logger{36} - %msg%n"
}
elseif ($Service -eq "products") {
    Write-Host "🔍 Iniciando PRODUCTS SERVICE con logs detallados...`n" -ForegroundColor Green
    java -jar products_service\target\products_service-0.0.1-SNAPSHOT.jar `
        --logging.level.root=INFO `
        --logging.level.com.linktic_test.products_service=DEBUG `
        --logging.level.org.springframework.web=DEBUG `
        --logging.level.org.hibernate.SQL=DEBUG `
        --logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE `
        --logging.pattern.console="%d{HH:mm:ss.SSS} [PRODUCTS] [%thread] %-5level %logger{36} - %msg%n"
}
elseif ($Service -eq "inventory") {
    Write-Host "🔍 Iniciando INVENTORY SERVICE con logs detallados...`n" -ForegroundColor Blue
    java -jar inventory_service\target\inventory_service-0.0.1-SNAPSHOT.jar `
        --logging.level.root=INFO `
        --logging.level.com.linktic_test.inventory_service=DEBUG `
        --logging.level.org.springframework.web=DEBUG `
        --logging.level.org.hibernate.SQL=DEBUG `
        --logging.pattern.console="%d{HH:mm:ss.SSS} [INVENTORY] [%thread] %-5level %logger{36} - %msg%n"
}
elseif ($Service -eq "orders") {
    Write-Host "🔍 Iniciando ORDERS SERVICE con logs detallados...`n" -ForegroundColor Yellow
    java -jar orders_service\target\orders_service-0.0.1-SNAPSHOT.jar `
        --logging.level.root=INFO `
        --logging.level.com.linktic_test.orders_service=DEBUG `
        --logging.level.org.springframework.web=DEBUG `
        --logging.level.org.springframework.kafka=DEBUG `
        --logging.level.org.hibernate.SQL=DEBUG `
        --logging.pattern.console="%d{HH:mm:ss.SSS} [ORDERS] [%thread] %-5level %logger{36} - %msg%n"
}
elseif ($Service -eq "notifications") {
    Write-Host "🔍 Iniciando NOTIFICATIONS SERVICE con logs detallados...`n" -ForegroundColor Cyan
    java -jar notifications_service\target\notifications_service-0.0.1-SNAPSHOT.jar `
        --logging.level.root=INFO `
        --logging.level.com.linktic_test.notifications_service=DEBUG `
        --logging.level.org.springframework.kafka=DEBUG `
        --logging.level.org.springframework.mail=DEBUG `
        --logging.pattern.console="%d{HH:mm:ss.SSS} [NOTIFICATIONS] [%thread] %-5level %logger{36} - %msg%n"
}
else {
    Write-Host "❌ Uso incorrecto. Especifica un servicio:" -ForegroundColor Red
    Write-Host ""
    Write-Host "Ejemplos:" -ForegroundColor Yellow
    Write-Host "  .\start-consolidated-logs.ps1 -Service eureka" -ForegroundColor White
    Write-Host "  .\start-consolidated-logs.ps1 -Service products" -ForegroundColor White
    Write-Host "  .\start-consolidated-logs.ps1 -Service inventory" -ForegroundColor White
    Write-Host "  .\start-consolidated-logs.ps1 -Service orders" -ForegroundColor White
    Write-Host "  .\start-consolidated-logs.ps1 -Service notifications" -ForegroundColor White
    Write-Host ""
    Write-Host "💡 Abre 5 terminales y ejecuta cada comando en una terminal diferente" -ForegroundColor Yellow
    Write-Host ""
}

