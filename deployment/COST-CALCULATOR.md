# 💰 Calculadora de Costos - Azure Deployment

## 📊 Costos Detallados por Servicio

### Azure Container Instances

| Componente | vCPU | RAM (GB) | Costo/Hora | Horas/Día | Costo/Día | Costo 2 Días | Costo 7 Días | Costo 30 Días |
|------------|------|----------|------------|-----------|-----------|--------------|--------------|---------------|
| **Eureka Server** | 1 | 1.5 | $0.0125 | 24 | $0.30 | $0.60 | $2.10 | $9.00 |
| **API Gateway** | 1 | 1.5 | $0.0125 | 24 | $0.30 | $0.60 | $2.10 | $9.00 |
| **Products Service** | 1 | 1.5 | $0.0125 | 24 | $0.30 | $0.60 | $2.10 | $9.00 |
| **Inventory Service** | 1 | 1.5 | $0.0125 | 24 | $0.30 | $0.60 | $2.10 | $9.00 |
| **Orders Service** | 1 | 1.5 | $0.0125 | 24 | $0.30 | $0.60 | $2.10 | $9.00 |
| **Notifications Service** | 1 | 1.5 | $0.0125 | 24 | $0.30 | $0.60 | $2.10 | $9.00 |
| **MySQL Container** | 1 | 2.0 | $0.0175 | 24 | $0.42 | $0.84 | $2.94 | $12.60 |
| **Subtotal Containers** | | | | | **$2.22** | **$4.44** | **$15.54** | **$66.60** |

### Otros Servicios Azure

| Servicio | Plan | Costo/Día | Costo 2 Días | Costo 7 Días | Costo 30 Días |
|----------|------|-----------|--------------|--------------|---------------|
| **Container Registry** | Basic | $0.167 | $0.35 | $1.17 | $5.00 |
| **Bandwidth (Egress)** | Primeros 100 GB | $0 | $0 | $0 | $0 |
| **Storage** | Incluido en ACR | $0 | $0 | $0 | $0 |
| **Subtotal Otros** | | **$0.17** | **$0.35** | **$1.17** | **$5.00** |

### Servicios Gratuitos

| Servicio | Costo | Límites |
|----------|-------|---------|
| **Supabase** | $0 | 500 MB DB, 1 GB storage, 2 GB bandwidth/mes |
| **Vercel** | $0 | 100 GB bandwidth/mes, 6000 min builds |

---

## 💵 Resumen de Costos Totales

| Período | Azure Containers | Azure ACR | **TOTAL AZURE** | Supabase | Vercel | **GRAN TOTAL** |
|---------|------------------|-----------|-----------------|----------|--------|----------------|
| **2 días** | $4.44 | $0.35 | **$4.79** | $0 | $0 | **$4.79** |
| **7 días** | $15.54 | $1.17 | **$16.71** | $0 | $0 | **$16.71** |
| **30 días** | $66.60 | $5.00 | **$71.60** | $0 | $0 | **$71.60** |

---

## 🎯 Con tu Presupuesto de $50/mes

### Escenario 1: Uso Continuo

| Días Corriendo | Costo | Restante |
|----------------|-------|----------|
| 2 días | $4.79 | **$45.21** |
| 5 días | $11.98 | **$38.02** |
| 10 días | $23.95 | **$26.05** |
| 15 días | $35.93 | **$14.07** |
| 20 días | $47.90 | **$2.10** |
| 21 días | $50.29 | **-$0.29** ⚠️ |

**Máximo con $50:** ~20-21 días continuos

---

### Escenario 2: Uso Intermitente (Apagar/Encender)

Si solo enciendes los servicios cuando los necesitas:

| Horas/Día Activo | Costo/Día | Días con $50 | Total Días Calendario |
|------------------|-----------|--------------|----------------------|
| 24h (continuo) | $2.39 | 20 días | 20 días |
| 12h/día | $1.20 | 41 días | 41 días |
| 8h/día | $0.80 | 62 días | 62 días |
| 4h/día | $0.40 | 125 días | 125 días |
| 2h/día | $0.20 | 250 días | 250 días |

**Estrategia recomendada para pruebas:**
- Encender solo cuando necesites probar
- Apagar al terminar el día
- Con 8h/día puedes tener 2 meses de pruebas

---

## 💡 Optimización de Costos

### Opción 1: Reducir Recursos (No Recomendado)

| Cambio | Ahorro/Día | Impacto |
|--------|------------|---------|
| Reducir RAM a 1 GB | -$0.30 | ⚠️ Puede causar OOM errors |
| Eliminar Notifications Service | -$0.30 | ⚠️ Pierde funcionalidad |
| Usar MySQL Gratis (Azure Database) | -$0.42 | ❌ No hay tier gratis |

**No recomendado:** La configuración actual es el mínimo viable.

---

### Opción 2: Apagar Servicios No Esenciales

Para desarrollo/pruebas, puedes apagar temporalmente:

| Servicio | Ahorro/Día | Funcionalidad Perdida |
|----------|------------|----------------------|
| Notifications Service | $0.30 | Emails de confirmación |
| Eureka Server* | $0.30 | Service discovery (deberás usar IPs directas) |

\* **No recomendado** apagar Eureka ya que los servicios no se encontrarán entre sí.

**Ahorro máximo seguro:** $0.30/día (sin notifications) = $9/mes

---

### Opción 3: Schedule Start/Stop

Automatizar el encendido/apagado con Azure Automation:

```bash
# Script para apagar todo (ejecutar al terminar el día)
az container stop --resource-group rg-linktic-ecommerce --name eureka-server
az container stop --resource-group rg-linktic-ecommerce --name api-gateway
az container stop --resource-group rg-linktic-ecommerce --name products-service
az container stop --resource-group rg-linktic-ecommerce --name inventory-service
az container stop --resource-group rg-linktic-ecommerce --name orders-service
az container stop --resource-group rg-linktic-ecommerce --name notifications-service
az container stop --resource-group rg-linktic-ecommerce --name mysql-db

# Script para encender todo (ejecutar al iniciar el día)
az container start --resource-group rg-linktic-ecommerce --name mysql-db
sleep 30
az container start --resource-group rg-linktic-ecommerce --name eureka-server
sleep 60
az container start --resource-group rg-linktic-ecommerce --name products-service
az container start --resource-group rg-linktic-ecommerce --name inventory-service
az container start --resource-group rg-linktic-ecommerce --name orders-service
az container start --resource-group rg-linktic-ecommerce --name notifications-service
sleep 30
az container start --resource-group rg-linktic-ecommerce --name api-gateway
```

**Ahorro:** Si solo usas 8h/día, ahorras 66% = $1.59/día = $47.70/mes

---

## 📅 Planificación de tu Uso (2 días)

### Plan Recomendado

**Día 1 (Despliegue):**
- 9:00 AM: Iniciar despliegue de Azure (1h)
- 10:00 AM: Servicios corriendo
- 10:30 AM: Desplegar Vercel (15 min)
- 11:00 AM - 6:00 PM: Pruebas y desarrollo (7h)
- 6:00 PM: Mantener encendido para demos

**Día 2 (Pruebas finales):**
- 9:00 AM - 6:00 PM: Más pruebas
- 6:00 PM: **ELIMINAR TODO** con `az group delete`

**Costo total:** $4.79 (48 horas continuas)
**O:** $3.19 (si apagas durante la noche: 32 horas activas)

---

## 🔮 Proyección de Costos para Producción

Si decidieras llevar esto a producción real:

### Opción: Azure App Service (Mejor para producción)

| Servicio | Plan | Costo/Mes | Descripción |
|----------|------|-----------|-------------|
| App Service Plan | B1 (1 core, 1.75 GB) | $13.14/app | 6 apps = $78.84 |
| Azure Database for MySQL | B1ms (1 vCore) | $21.17 | Managed, backups automáticos |
| Azure Cache for Redis | C0 (250 MB) | $16.06 | Para sesiones |
| Application Insights | Primeros 5 GB gratis | ~$10 | Monitoring |
| Azure Event Grid | Primeros 100K gratis | ~$5 | Para eventos |
| **TOTAL Producción** | | **~$131/mes** | Con alta disponibilidad |

### Opción: Optimizada con AKS (Kubernetes)

| Servicio | Costo/Mes | Descripción |
|----------|-----------|-------------|
| AKS Cluster | $73 | Nodos B2s (2 vCPU, 4 GB) x 2 |
| Azure Database for MySQL | $21 | Managed DB |
| Load Balancer | $18 | Balanceo externo |
| Storage | $10 | Persistent volumes |
| **TOTAL AKS** | **~$122/mes** | Escalable, resiliente |

---

## 📊 Comparación con Alternativas

### Competidores de Cloud

| Proveedor | Servicio Similar | Costo 2 Días | Costo/Mes |
|-----------|------------------|--------------|-----------|
| **Azure** | Container Instances | $4.79 | $71.60 |
| **AWS** | ECS Fargate | ~$6.50 | ~$97.00 |
| **GCP** | Cloud Run | ~$5.20 | ~$78.00 |
| **Heroku** | Hobby Dynos (7 dynos) | ~$7.00 | ~$49.00 |
| **Railway** | Pro Plan | ~$5.00 | ~$20.00 |
| **Render** | Individual (6 services) | ~$8.00 | ~$42.00 |

**Azure Container Instances es competitivo** para uso temporal.

---

## 🎯 Recomendaciones Finales

### Para tu caso (2 días de prueba):

✅ **Usar exactamente como está configurado**
- Costo: $4.79
- No optimizar para ahorrar $1-2, no vale la pena el esfuerzo
- Los $50 son más que suficientes

### Si decides extender más allá de 2 días:

1. **3-7 días:** Mantén todo encendido ($16.71 total)
2. **1-2 semanas:** Apaga por las noches ($25-30 total)
3. **1 mes:** Usa script de start/stop ($35-40 total)

### Después de las pruebas:

🗑️ **ELIMINAR TODO para evitar cargos**
```bash
az group delete --name rg-linktic-ecommerce --yes --no-wait
```

---

## 📞 Monitoreo de Costos

### Configurar Alertas en Azure

```bash
# Crear alerta cuando el gasto supere $10
az monitor metrics alert create \
  --name "Alerta-Costo-$10" \
  --resource-group rg-linktic-ecommerce \
  --condition "total cost > 10" \
  --description "Gasto superó los $10 USD"

# Ver gasto actual
az consumption usage list --output table
```

### Ver Costo Acumulado

1. Ve al portal de Azure
2. Cost Management + Billing → Cost Analysis
3. Filtra por Resource Group: `rg-linktic-ecommerce`
4. Verás el gasto en tiempo real

---

## ✅ Checklist de Control de Costos

- [ ] Configurar alerta de costos en Azure Portal
- [ ] Apagar servicios al terminar el día (opcional)
- [ ] Eliminar recursos después de las pruebas
- [ ] Verificar que no haya recursos huérfanos:
  ```bash
  az resource list --resource-group rg-linktic-ecommerce
  ```
- [ ] Confirmar eliminación del Resource Group:
  ```bash
  az group list --query "[?name=='rg-linktic-ecommerce']" --output table
  # Debe estar vacío
  ```

---

**Con $50 en Azure para 2 días: PERFECTO** ✅

**Costo real:** $4.79
**Crédito restante:** $45.21
**Suficiente para:** ~20 días continuos o ~2 meses con uso intermitente

🎉 **¡No te preocupes por el costo, tienes de sobra!**
