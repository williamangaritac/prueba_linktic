#!/bin/bash

# ============================================
# SCRIPT DE DESPLIEGUE EN AZURE
# ============================================
# Este script despliega la aplicación en Azure usando Container Instances
# Requiere: Azure CLI instalado y configurado

set -e  # Salir en caso de error

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para logging
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# ============================================
# CONFIGURACIÓN
# ============================================

# Cargar variables de entorno
if [ -f ".env.azure" ]; then
    log_info "Cargando variables de entorno desde .env.azure..."
    export $(cat .env.azure | grep -v '^#' | xargs)
else
    log_error "Archivo .env.azure no encontrado. Por favor, créalo basándote en .env.azure.example"
    exit 1
fi

# Variables por defecto
RESOURCE_GROUP=${AZURE_RESOURCE_GROUP:-"rg-linktic-ecommerce"}
LOCATION=${AZURE_LOCATION:-"eastus"}
ACR_NAME=${AZURE_CONTAINER_REGISTRY:-"linkticacr"}
APP_NAME="linktic-ecommerce"

log_info "Configuración de despliegue:"
echo "  Resource Group: $RESOURCE_GROUP"
echo "  Location: $LOCATION"
echo "  Container Registry: $ACR_NAME"

# ============================================
# PASO 1: VERIFICAR AZURE CLI
# ============================================

log_info "Verificando Azure CLI..."
if ! command -v az &> /dev/null; then
    log_error "Azure CLI no está instalado. Instálalo desde: https://docs.microsoft.com/cli/azure/install-azure-cli"
    exit 1
fi

log_info "Iniciando sesión en Azure..."
az account show &> /dev/null || az login

# ============================================
# PASO 2: CREAR RESOURCE GROUP
# ============================================

log_info "Creando Resource Group..."
az group create \
    --name $RESOURCE_GROUP \
    --location $LOCATION \
    --output table

# ============================================
# PASO 3: CREAR AZURE CONTAINER REGISTRY
# ============================================

log_info "Creando Azure Container Registry..."
az acr create \
    --resource-group $RESOURCE_GROUP \
    --name $ACR_NAME \
    --sku Basic \
    --admin-enabled true \
    --output table

log_info "Obteniendo credenciales del ACR..."
ACR_USERNAME=$(az acr credential show --name $ACR_NAME --query "username" -o tsv)
ACR_PASSWORD=$(az acr credential show --name $ACR_NAME --query "passwords[0].value" -o tsv)
ACR_LOGIN_SERVER=$(az acr show --name $ACR_NAME --query "loginServer" -o tsv)

log_success "ACR creado: $ACR_LOGIN_SERVER"

# ============================================
# PASO 4: BUILD Y PUSH DE IMÁGENES
# ============================================

log_info "Iniciando sesión en ACR..."
echo $ACR_PASSWORD | docker login $ACR_LOGIN_SERVER --username $ACR_USERNAME --password-stdin

# Servicios a construir
SERVICES=(
    "eureka-server"
    "api-gateway"
    "products_service"
    "inventory_service"
    "orders_service"
    "notifications_service"
)

log_info "Construyendo y subiendo imágenes de microservicios..."

for SERVICE in "${SERVICES[@]}"; do
    log_info "Construyendo $SERVICE..."

    # Navegar al directorio del servicio
    cd ../../microservices_linktic/$SERVICE

    # Build de la imagen
    IMAGE_NAME="$ACR_LOGIN_SERVER/$APP_NAME-$SERVICE:latest"
    docker build -t $IMAGE_NAME .

    # Push al ACR
    log_info "Subiendo $SERVICE a ACR..."
    docker push $IMAGE_NAME

    log_success "$SERVICE desplegado: $IMAGE_NAME"

    # Volver al directorio de deployment
    cd ../../deployment/azure
done

# ============================================
# PASO 5: DESPLEGAR MYSQL
# ============================================

log_info "Desplegando MySQL..."

az container create \
    --resource-group $RESOURCE_GROUP \
    --name mysql-db \
    --image mysql:8.0 \
    --dns-name-label $APP_NAME-mysql \
    --ports 3306 \
    --cpu 1 \
    --memory 2 \
    --environment-variables \
        MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD \
        MYSQL_DATABASE=linktic_orders \
        MYSQL_USER=linktic \
        MYSQL_PASSWORD=$MYSQL_PASSWORD \
    --output table

MYSQL_FQDN=$(az container show --resource-group $RESOURCE_GROUP --name mysql-db --query "ipAddress.fqdn" -o tsv)
log_success "MySQL desplegado en: $MYSQL_FQDN"

# ============================================
# PASO 6: DESPLEGAR EUREKA SERVER
# ============================================

log_info "Desplegando Eureka Server..."

az container create \
    --resource-group $RESOURCE_GROUP \
    --name eureka-server \
    --image "$ACR_LOGIN_SERVER/$APP_NAME-eureka-server:latest" \
    --dns-name-label $APP_NAME-eureka \
    --ports 8761 \
    --cpu 1 \
    --memory 1.5 \
    --registry-login-server $ACR_LOGIN_SERVER \
    --registry-username $ACR_USERNAME \
    --registry-password $ACR_PASSWORD \
    --environment-variables \
        SPRING_PROFILES_ACTIVE=prod \
    --output table

EUREKA_FQDN=$(az container show --resource-group $RESOURCE_GROUP --name eureka-server --query "ipAddress.fqdn" -o tsv)
log_success "Eureka Server desplegado en: http://$EUREKA_FQDN:8761"

# Esperar a que Eureka esté listo
log_info "Esperando a que Eureka Server esté listo..."
sleep 60

# ============================================
# PASO 7: DESPLEGAR MICROSERVICIOS
# ============================================

# Products Service
log_info "Desplegando Products Service..."
az container create \
    --resource-group $RESOURCE_GROUP \
    --name products-service \
    --image "$ACR_LOGIN_SERVER/$APP_NAME-products_service:latest" \
    --dns-name-label $APP_NAME-products \
    --ports 8081 \
    --cpu 1 \
    --memory 1.5 \
    --registry-login-server $ACR_LOGIN_SERVER \
    --registry-username $ACR_USERNAME \
    --registry-password $ACR_PASSWORD \
    --environment-variables \
        SPRING_PROFILES_ACTIVE=prod \
        SPRING_DATASOURCE_URL="$PRODUCTS_DB_URL" \
        SPRING_DATASOURCE_USERNAME="$PRODUCTS_DB_USER" \
        SPRING_DATASOURCE_PASSWORD="$PRODUCTS_DB_PASSWORD" \
        EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="http://$EUREKA_FQDN:8761/eureka/" \
    --output table

# Inventory Service
log_info "Desplegando Inventory Service..."
az container create \
    --resource-group $RESOURCE_GROUP \
    --name inventory-service \
    --image "$ACR_LOGIN_SERVER/$APP_NAME-inventory_service:latest" \
    --dns-name-label $APP_NAME-inventory \
    --ports 8082 \
    --cpu 1 \
    --memory 1.5 \
    --registry-login-server $ACR_LOGIN_SERVER \
    --registry-username $ACR_USERNAME \
    --registry-password $ACR_PASSWORD \
    --environment-variables \
        SPRING_PROFILES_ACTIVE=prod \
        SPRING_DATASOURCE_URL="$INVENTORY_DB_URL" \
        SPRING_DATASOURCE_USERNAME="$INVENTORY_DB_USER" \
        SPRING_DATASOURCE_PASSWORD="$INVENTORY_DB_PASSWORD" \
        EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="http://$EUREKA_FQDN:8761/eureka/" \
    --output table

# Orders Service
log_info "Desplegando Orders Service..."
az container create \
    --resource-group $RESOURCE_GROUP \
    --name orders-service \
    --image "$ACR_LOGIN_SERVER/$APP_NAME-orders_service:latest" \
    --dns-name-label $APP_NAME-orders \
    --ports 8083 \
    --cpu 1 \
    --memory 1.5 \
    --registry-login-server $ACR_LOGIN_SERVER \
    --registry-username $ACR_USERNAME \
    --registry-password $ACR_PASSWORD \
    --environment-variables \
        SPRING_PROFILES_ACTIVE=prod \
        SPRING_DATASOURCE_URL="jdbc:mysql://$MYSQL_FQDN:3306/linktic_orders" \
        SPRING_DATASOURCE_USERNAME=linktic \
        SPRING_DATASOURCE_PASSWORD="$MYSQL_PASSWORD" \
        EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="http://$EUREKA_FQDN:8761/eureka/" \
        SPRING_KAFKA_ENABLED=false \
    --output table

# Notifications Service
log_info "Desplegando Notifications Service..."
az container create \
    --resource-group $RESOURCE_GROUP \
    --name notifications-service \
    --image "$ACR_LOGIN_SERVER/$APP_NAME-notifications_service:latest" \
    --dns-name-label $APP_NAME-notifications \
    --ports 8084 \
    --cpu 1 \
    --memory 1.5 \
    --registry-login-server $ACR_LOGIN_SERVER \
    --registry-username $ACR_USERNAME \
    --registry-password $ACR_PASSWORD \
    --environment-variables \
        SPRING_PROFILES_ACTIVE=prod \
        SPRING_DATASOURCE_URL="$NOTIFICATIONS_DB_URL" \
        SPRING_DATASOURCE_USERNAME="$NOTIFICATIONS_DB_USER" \
        SPRING_DATASOURCE_PASSWORD="$NOTIFICATIONS_DB_PASSWORD" \
        EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="http://$EUREKA_FQDN:8761/eureka/" \
        MAIL_USERNAME="$MAIL_USERNAME" \
        MAIL_PASSWORD="$MAIL_PASSWORD" \
        SPRING_KAFKA_ENABLED=false \
    --output table

# ============================================
# PASO 8: DESPLEGAR API GATEWAY
# ============================================

log_info "Desplegando API Gateway..."
az container create \
    --resource-group $RESOURCE_GROUP \
    --name api-gateway \
    --image "$ACR_LOGIN_SERVER/$APP_NAME-api-gateway:latest" \
    --dns-name-label $APP_NAME-gateway \
    --ports 8080 \
    --cpu 1 \
    --memory 1.5 \
    --registry-login-server $ACR_LOGIN_SERVER \
    --registry-username $ACR_USERNAME \
    --registry-password $ACR_PASSWORD \
    --environment-variables \
        SPRING_PROFILES_ACTIVE=prod \
        EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="http://$EUREKA_FQDN:8761/eureka/" \
    --output table

GATEWAY_FQDN=$(az container show --resource-group $RESOURCE_GROUP --name api-gateway --query "ipAddress.fqdn" -o tsv)

# ============================================
# RESUMEN
# ============================================

log_success "============================================"
log_success "¡DESPLIEGUE COMPLETADO!"
log_success "============================================"
echo ""
echo "URLs de acceso:"
echo "  • Eureka Server:   http://$EUREKA_FQDN:8761"
echo "  • API Gateway:     http://$GATEWAY_FQDN:8080"
echo "  • Products API:    http://$GATEWAY_FQDN:8080/api/v1/products"
echo "  • Inventory API:   http://$GATEWAY_FQDN:8080/api/v1/inventory"
echo "  • Orders API:      http://$GATEWAY_FQDN:8080/api/v1/orders"
echo ""
echo "Configuración para Vercel (frontend):"
echo "  • API_GATEWAY_URL: http://$GATEWAY_FQDN:8080"
echo ""
log_warning "IMPORTANTE: Guarda la URL del API Gateway para configurar el frontend en Vercel"
echo ""

# Guardar configuración
cat > deployment-info.txt <<EOF
Información de Despliegue - $(date)
====================================

Resource Group: $RESOURCE_GROUP
Location: $LOCATION
Container Registry: $ACR_LOGIN_SERVER

URLs:
- Eureka Server: http://$EUREKA_FQDN:8761
- API Gateway: http://$GATEWAY_FQDN:8080
- MySQL: $MYSQL_FQDN:3306

Variables de entorno para Vercel:
VITE_API_GATEWAY_URL=http://$GATEWAY_FQDN:8080
EOF

log_success "Información guardada en deployment-info.txt"

# ============================================
# COMANDOS ÚTILES
# ============================================

echo ""
log_info "Comandos útiles:"
echo "  • Ver logs: az container logs --resource-group $RESOURCE_GROUP --name [CONTAINER_NAME]"
echo "  • Ver estado: az container show --resource-group $RESOURCE_GROUP --name [CONTAINER_NAME]"
echo "  • Eliminar todo: az group delete --name $RESOURCE_GROUP --yes --no-wait"
echo ""
