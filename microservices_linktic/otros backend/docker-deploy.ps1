# Script para desplegar todo el proyecto con Docker Compose

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  DESPLEGANDO FULLSTACK CON DOCKER" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

# Verificar que Docker esté corriendo
Write-Host "🔍 Verificando Docker..." -ForegroundColor Cyan
try {
    docker --version | Out-Null
    Write-Host "✅ Docker instalado" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "   Instala Docker Desktop desde: https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
    exit 1
}

try {
    docker ps | Out-Null
    Write-Host "✅ Docker está corriendo" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker no está corriendo" -ForegroundColor Red
    Write-Host "   Inicia Docker Desktop y vuelve a intentar" -ForegroundColor Yellow
    exit 1
}

# Detener servicios locales que puedan estar corriendo
Write-Host "`n🛑 Deteniendo servicios locales..." -ForegroundColor Yellow
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Stop-Process -Name node -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2
Write-Host "✅ Servicios locales detenidos" -ForegroundColor Green

# Detener y eliminar contenedores existentes
Write-Host "`n🧹 Limpiando contenedores existentes..." -ForegroundColor Yellow
docker-compose down -v 2>$null
Write-Host "✅ Contenedores eliminados" -ForegroundColor Green

# Construir imágenes
Write-Host "`n🔨 Construyendo imágenes Docker..." -ForegroundColor Cyan
Write-Host "   (Primera vez: 3-5 minutos | Siguientes veces: 30 segundos)`n" -ForegroundColor Yellow

docker-compose build

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n❌ Error al construir las imágenes" -ForegroundColor Red
    exit 1
}

Write-Host "`n✅ Imágenes construidas exitosamente" -ForegroundColor Green

# Levantar los servicios
Write-Host "`n🚀 Levantando servicios..." -ForegroundColor Cyan
Write-Host "   (Iniciando en paralelo para mayor velocidad)`n" -ForegroundColor Yellow

docker-compose up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n❌ Error al levantar los servicios" -ForegroundColor Red
    exit 1
}

Write-Host "`n✅ Servicios iniciados" -ForegroundColor Green

# Esperar a que los servicios estén listos
Write-Host "`n⏳ Esperando a que los servicios estén listos..." -ForegroundColor Yellow
Write-Host "   (60 segundos - optimizado)`n" -ForegroundColor Cyan

Start-Sleep -Seconds 60

# Verificar estado de los contenedores
Write-Host "`n📊 Estado de los contenedores:" -ForegroundColor Cyan
docker-compose ps

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  DESPLIEGUE COMPLETADO" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "📝 URLs de los servicios:" -ForegroundColor Yellow
Write-Host "`n🎨 FRONTEND:" -ForegroundColor Cyan
Write-Host "   • Angular App: http://localhost:4200" -ForegroundColor White

Write-Host "`n🔧 BACKEND:" -ForegroundColor Cyan
Write-Host "   • Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "   • Products Service: http://localhost:8081/api/v1/frontend/products?page=0" -ForegroundColor White
Write-Host "   • Inventory Service: http://localhost:8082" -ForegroundColor White
Write-Host "   • Orders Service: http://localhost:8083" -ForegroundColor White
Write-Host "   • Notifications Service: http://localhost:8084" -ForegroundColor White

Write-Host "`n💾 BASES DE DATOS:" -ForegroundColor Cyan
Write-Host "   • PostgreSQL: localhost:5432 (usuario: postgres, password: postgres)" -ForegroundColor White
Write-Host "   • MySQL: localhost:3306 (usuario: linktic, password: linktic123)" -ForegroundColor White

Write-Host "`n💡 COMANDOS ÚTILES:" -ForegroundColor Yellow
Write-Host "   • Ver logs de todos los servicios: docker-compose logs -f" -ForegroundColor White
Write-Host "   • Ver logs de un servicio: docker-compose logs -f [nombre-servicio]" -ForegroundColor White
Write-Host "   • Detener todos los servicios: docker-compose down" -ForegroundColor White
Write-Host "   • Reiniciar un servicio: docker-compose restart [nombre-servicio]" -ForegroundColor White
Write-Host "   • Ver estado: docker-compose ps" -ForegroundColor White

Write-Host "`n🧪 PROBAR EL SISTEMA:" -ForegroundColor Cyan
Write-Host "   1. Abre http://localhost:4200 en tu navegador" -ForegroundColor White
Write-Host "   2. Verás el catálogo de productos con precios" -ForegroundColor White
Write-Host "   3. Prueba los botones 'Ver Detalles' y 'Comprar'" -ForegroundColor White
Write-Host "   4. Revisa Eureka en http://localhost:8761 para ver los servicios registrados`n" -ForegroundColor White

Write-Host "========================================`n" -ForegroundColor Cyan

