# Script para probar todos los endpoints del backend

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  PROBANDO ENDPOINTS DEL BACKEND" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

# Test 1: Listar productos (página 0)
Write-Host "1️⃣  GET /api/v1/frontend/products?page=0" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/v1/frontend/products?page=0" -Method Get -TimeoutSec 5
    Write-Host "   ✅ Status: OK" -ForegroundColor Green
    Write-Host "   📦 Total productos: $($response.totalElements)" -ForegroundColor White
    Write-Host "   📄 Total páginas: $($response.totalPages)" -ForegroundColor White
    Write-Host "   📊 Productos en página: $($response.content.Count)" -ForegroundColor White
    if ($response.content.Count -gt 0) {
        Write-Host "   🏷️  Primer producto: $($response.content[0].name)" -ForegroundColor White
    }
} catch {
    Write-Host "   ❌ ERROR" -ForegroundColor Red
}

# Test 2: Ver detalles de producto
Write-Host "`n2️⃣  GET /api/v1/frontend/products/sku/{sku}" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/v1/frontend/products/sku/29444ed7a8f8495587365a6b61458735" -Method Get -TimeoutSec 5
    Write-Host "   ✅ Status: OK" -ForegroundColor Green
    Write-Host "   📦 Producto: $($response.name)" -ForegroundColor White
    Write-Host "   💰 Precio: `$$($response.price)" -ForegroundColor White
    Write-Host "   📝 Descripción: $($response.description.Substring(0, [Math]::Min(50, $response.description.Length)))..." -ForegroundColor White
} catch {
    Write-Host "   ❌ ERROR" -ForegroundColor Red
}

# Test 3: Health checks
Write-Host "`n3️⃣  Health Checks" -ForegroundColor Cyan

$services = @(
    @{Name="Products"; Port=8081},
    @{Name="Inventory"; Port=8082},
    @{Name="Orders"; Port=8083},
    @{Name="Notifications"; Port=8084}
)

foreach ($service in $services) {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:$($service.Port)/api/v1/actuator/health" -Method Get -TimeoutSec 3
        Write-Host "   ✅ $($service.Name) Service - $($response.status)" -ForegroundColor Green
    } catch {
        Write-Host "   ❌ $($service.Name) Service - ERROR" -ForegroundColor Red
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  ✅ PRUEBAS COMPLETADAS" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "📍 URLs para el Frontend Angular:" -ForegroundColor Yellow
Write-Host "   • Listar productos: http://localhost:8081/api/v1/frontend/products?page=0" -ForegroundColor White
Write-Host "   • Ver detalles: http://localhost:8081/api/v1/frontend/products/sku/{sku}" -ForegroundColor White
Write-Host "   • Crear orden: http://localhost:8083/api/v1/frontend/orders/purchase" -ForegroundColor White
Write-Host ""

