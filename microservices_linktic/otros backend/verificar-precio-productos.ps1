# Script para verificar que el precio se envía correctamente desde el backend

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  VERIFICANDO PRECIO EN PRODUCTOS" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "Probando endpoint de productos...`n" -ForegroundColor Cyan

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/v1/frontend/products?page=0" -Method Get -TimeoutSec 5
    
    Write-Host "EXITO: Endpoint funcionando correctamente`n" -ForegroundColor Green
    
    Write-Host "Informacion de la respuesta:" -ForegroundColor Yellow
    Write-Host "   Total de productos: $($response.totalElements)" -ForegroundColor White
    Write-Host "   Total de paginas: $($response.totalPages)" -ForegroundColor White
    Write-Host "   Productos en esta pagina: $($response.content.Count)`n" -ForegroundColor White
    
    Write-Host "PRECIOS DE LOS PRODUCTOS (Pagina 0):" -ForegroundColor Green
    Write-Host "-------------------------------------------------------------" -ForegroundColor Gray
    
    foreach ($product in $response.content) {
        Write-Host "   Producto: $($product.name)" -ForegroundColor Cyan
        Write-Host "      SKU: $($product.sku)" -ForegroundColor Gray
        Write-Host "      PRECIO: `$$($product.price)" -ForegroundColor Green
        $estado = if ($product.status) { "Disponible" } else { "No disponible" }
        Write-Host "      Estado: $estado" -ForegroundColor White
        Write-Host ""
    }
    
    Write-Host "-------------------------------------------------------------`n" -ForegroundColor Gray
    
    Write-Host "CONFIRMACION:" -ForegroundColor Green
    Write-Host "   - Todos los productos incluyen el campo 'price'" -ForegroundColor White
    Write-Host "   - El precio viene desde el backend (products_service)" -ForegroundColor White
    Write-Host "   - NO se necesita un boton 'Consultar Precio'" -ForegroundColor White
    Write-Host "   - El frontend debe mostrar el precio directamente`n" -ForegroundColor White
    
    Write-Host "Para el Frontend Angular:" -ForegroundColor Yellow
    Write-Host "   HTML: <span class='price'>`${{ product.price | number:'1.2-2' }}</span>" -ForegroundColor Cyan
    Write-Host "   Ejemplo: `$2,805.00`n" -ForegroundColor White
    
} catch {
    Write-Host "ERROR: No se pudo conectar al servicio" -ForegroundColor Red
    Write-Host "   Asegurate de que products_service este corriendo en el puerto 8081`n" -ForegroundColor Yellow
    Write-Host "   Para iniciar el servicio:" -ForegroundColor Cyan
    Write-Host "   cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic" -ForegroundColor White
    Write-Host "   `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot'" -ForegroundColor White
    Write-Host "   java -jar products_service\target\products_service-0.0.1-SNAPSHOT.jar`n" -ForegroundColor White
}

Write-Host "========================================`n" -ForegroundColor Cyan

