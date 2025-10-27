# Script para desplegar todo el proyecto con Docker Compose

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  DESPLEGANDO FULLSTACK CON DOCKER" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

# Verificar que Docker este corriendo
Write-Host "Verificando Docker..." -ForegroundColor Cyan
try {
    docker --version | Out-Null
    Write-Host "[OK] Docker instalado" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Docker no esta instalado o no esta en el PATH" -ForegroundColor Red
    Write-Host "   Instala Docker Desktop desde: https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
    exit 1
}

try {
    docker ps | Out-Null
    Write-Host "[OK] Docker esta corriendo" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Docker no esta corriendo" -ForegroundColor Red
    Write-Host "   Inicia Docker Desktop y vuelve a intentar" -ForegroundColor Yellow
    exit 1
}

# Detener servicios locales que puedan estar corriendo
Write-Host "`nDeteniendo servicios locales..." -ForegroundColor Yellow
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Stop-Process -Name node -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2
Write-Host "[OK] Servicios locales detenidos" -ForegroundColor Green

# Detener y eliminar contenedores existentes
Write-Host "`nLimpiando contenedores existentes..." -ForegroundColor Yellow
docker-compose down -v 2>$null
Write-Host "[OK] Contenedores eliminados" -ForegroundColor Green

# Construir imagenes
Write-Host "`nConstruyendo imagenes Docker..." -ForegroundColor Cyan
Write-Host "   (Primera vez: 3-5 minutos | Siguientes veces: 30 segundos)`n" -ForegroundColor Yellow

docker-compose build

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[ERROR] Error al construir las imagenes" -ForegroundColor Red
    exit 1
}

Write-Host "`n[OK] Imagenes construidas exitosamente" -ForegroundColor Green

# Levantar los servicios
Write-Host "`nLevantando servicios..." -ForegroundColor Cyan
Write-Host "   (Iniciando en paralelo para mayor velocidad)`n" -ForegroundColor Yellow

docker-compose up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[ERROR] Error al levantar los servicios" -ForegroundColor Red
    exit 1
}

Write-Host "`n[OK] Servicios iniciados" -ForegroundColor Green

# Esperar a que los servicios esten listos
Write-Host "`nEsperando a que los servicios esten listos..." -ForegroundColor Yellow
Write-Host "   (60 segundos - optimizado)`n" -ForegroundColor Cyan

Start-Sleep -Seconds 60

# Verificar estado de los contenedores
Write-Host "`nEstado de los contenedores:" -ForegroundColor Cyan
docker-compose ps

# Esperar a que Swagger Aggregator este listo
Write-Host "`nEsperando a que Swagger Aggregator este disponible..." -ForegroundColor Yellow
$maxAttempts = 30
$attempt = 0
$swaggerReady = $false

while ($attempt -lt $maxAttempts -and -not $swaggerReady) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8090" -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            $swaggerReady = $true
            Write-Host "[OK] Swagger Aggregator esta listo" -ForegroundColor Green
        }
    } catch {
        $attempt++
        Start-Sleep -Seconds 1
    }
}

if (-not $swaggerReady) {
    Write-Host "[ADVERTENCIA] Swagger Aggregator no respondio en el tiempo esperado, continuando..." -ForegroundColor Yellow
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  DESPLIEGUE COMPLETADO" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "URLs de los servicios:" -ForegroundColor Yellow
Write-Host "`nFRONTEND:" -ForegroundColor Cyan
Write-Host "   - Angular App: http://localhost:4200" -ForegroundColor White

Write-Host "`nDOCUMENTACION API:" -ForegroundColor Cyan
Write-Host "   - Swagger Aggregator: http://localhost:8090" -ForegroundColor White
Write-Host "   - Products Service: http://localhost:8081/api/v1/swagger-ui.html" -ForegroundColor White
Write-Host "   - Inventory Service: http://localhost:8082/api/v1/swagger-ui.html" -ForegroundColor White
Write-Host "   - Orders Service: http://localhost:8083/api/v1/swagger-ui.html" -ForegroundColor White
Write-Host "   - Notifications Service: http://localhost:8084/api/v1/swagger-ui.html" -ForegroundColor White

Write-Host "`nBACKEND:" -ForegroundColor Cyan
Write-Host "   - Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "   - Products Service: http://localhost:8081/api/v1/frontend/products?page=0" -ForegroundColor White
Write-Host "   - Inventory Service: http://localhost:8082" -ForegroundColor White
Write-Host "   - Orders Service: http://localhost:8083" -ForegroundColor White
Write-Host "   - Notifications Service: http://localhost:8084" -ForegroundColor White

Write-Host "`nBASES DE DATOS:" -ForegroundColor Cyan
Write-Host "   - PostgreSQL: localhost:5432 (usuario: postgres, password: postgres)" -ForegroundColor White
Write-Host "   - MySQL: localhost:3306 (usuario: linktic, password: linktic123)" -ForegroundColor White

Write-Host "`nCOMANDOS UTILES:" -ForegroundColor Yellow
Write-Host "   - Ver logs de todos los servicios: docker-compose logs -f" -ForegroundColor White
Write-Host "   - Ver logs de un servicio: docker-compose logs -f [nombre-servicio]" -ForegroundColor White
Write-Host "   - Detener todos los servicios: docker-compose down" -ForegroundColor White
Write-Host "   - Reiniciar un servicio: docker-compose restart [nombre-servicio]" -ForegroundColor White
Write-Host "   - Ver estado: docker-compose ps" -ForegroundColor White

Write-Host "`nPROBAR EL SISTEMA:" -ForegroundColor Cyan
Write-Host "   1. Abre http://localhost:4200 en tu navegador" -ForegroundColor White
Write-Host "   2. Veras el catalogo de productos con precios" -ForegroundColor White
Write-Host "   3. Prueba los botones Ver Detalles y Comprar" -ForegroundColor White
Write-Host "   4. Revisa Eureka en http://localhost:8761 para ver los servicios registrados" -ForegroundColor White
Write-Host "   5. Accede a Swagger Aggregator en http://localhost:8090 para ver todas las APIs`n" -ForegroundColor White

Write-Host "========================================`n" -ForegroundColor Cyan

# Abrir navegador automaticamente con 2 pestanas
Write-Host "`nAbriendo navegador automaticamente..." -ForegroundColor Cyan

$urls = @(
    "http://localhost:4200",      # Frontend Angular
    "http://localhost:8090"       # Swagger Aggregator (todos los microservicios)
)

foreach ($url in $urls) {
    Write-Host "   Abriendo: $url" -ForegroundColor Yellow
    Start-Process $url
    Start-Sleep -Seconds 2
}

Write-Host "`n[OK] Navegador abierto con 2 pestanas:" -ForegroundColor Green
Write-Host "   1. Frontend Angular (http://localhost:4200)" -ForegroundColor White
Write-Host "   2. Swagger Aggregator - Todos los microservicios (http://localhost:8090)" -ForegroundColor White
Write-Host "`nTip: El Swagger Aggregator contiene enlaces a todos los Swagger de los microservicios" -ForegroundColor Cyan
Write-Host "`n========================================`n" -ForegroundColor Cyan

