# ⚡ Guía Rápida de Despliegue - 2 Días

## 🎯 Respuestas Directas

### ¿Cuánto dura Supabase gratis?
**INDEFINIDO** ✅ No expira nunca. Límite: 500 MB DB, se pausa después de 1 semana sin uso (se reactiva en segundos).

### ¿Cuánto dura Vercel gratis?
**INDEFINIDO** ✅ No expira nunca. Límite: 100 GB bandwidth/mes, 6000 min builds/mes.

### ¿Es suficiente $50/mes en Azure para 2 días?
**SÍ, MÁS QUE SUFICIENTE** ✅ Costo estimado 2 días: ~$5 USD. Te sobran $45.

---

## 🚀 Despliegue en 1 Hora (Versión Express)

### Paso 1: Supabase (10 min)

```bash
# 1. Crea 3 proyectos en https://supabase.com:
#    - linktic-products
#    - linktic-inventory
#    - linktic-notifications

# 2. En cada proyecto, ve a SQL Editor y ejecuta:
#    - Proyecto 1: deployment/supabase/01-products-db.sql
#    - Proyecto 2: deployment/supabase/02-inventory-db.sql
#    - Proyecto 3: deployment/supabase/03-notifications-db.sql

# 3. Guarda las 3 connection strings (Settings → Database)
```

---

### Paso 2: Azure (40 min)

```bash
cd deployment/azure

# 1. Configurar variables
cp .env.azure.example .env.azure
nano .env.azure  # Completa con tus valores de Supabase

# 2. Login en Azure
az login

# 3. Desplegar (automático)
chmod +x deploy-to-azure.sh
./deploy-to-azure.sh

# 4. Guarda la URL del API Gateway que aparece al final
cat deployment-info.txt
```

**Tiempo de espera:** 30-40 minutos (build de imágenes)

---

### Paso 3: Vercel (10 min)

```bash
# 1. Push a Git (si no lo has hecho)
git add .
git commit -m "feat: Deployment configuration"
git push origin main

# 2. Ve a https://vercel.com
# 3. Import Project → Selecciona tu repo
# 4. Configura:
#    - Root Directory: frontend_angular
#    - Framework: Angular
#    - Environment Variables:
#      API_GATEWAY_URL=http://[TU_AZURE_GATEWAY]:8080

# 5. Deploy
```

---

## 🧪 Prueba Rápida

```bash
# 1. Abre tu URL de Vercel
https://tu-proyecto.vercel.app

# 2. Verifica que cargue el catálogo de 15 productos

# 3. Realiza una compra de prueba:
#    - Click en "Ver Detalles"
#    - Click en "Comprar"
#    - Ingresa email y cantidad
#    - Confirma

# 4. Verifica que el stock se reduzca
```

---

## 💰 Costo Real (2 días)

| Servicio | Costo |
|----------|-------|
| Supabase | **$0** |
| Vercel | **$0** |
| Azure | **$4.79** |
| **TOTAL** | **$4.79** |

**Te sobran:** $45.21 de tu crédito de $50

---

## 🗑️ Limpiar (Después de 2 días)

```bash
# Eliminar Azure (MUY IMPORTANTE para no gastar más)
az group delete --name rg-linktic-ecommerce --yes --no-wait

# Eliminar Vercel (opcional)
# Dashboard → Settings → Delete Project

# Supabase se pausa solo, no hace falta eliminarlo
```

---

## 📱 URLs Importantes

Después del despliegue, tendrás:

```
Frontend (Vercel):
https://tu-proyecto.vercel.app

Backend (Azure):
http://[tu-gateway].eastus.azurecontainer.io:8080

API Endpoints:
- Productos: /api/v1/products
- Inventario: /api/v1/inventory
- Órdenes: /api/v1/orders
- Notificaciones: /api/v1/notifications

Monitoring:
- Eureka: http://[tu-eureka].eastus.azurecontainer.io:8761
```

---

## 🆘 Problemas Comunes

### Frontend no carga productos

```bash
# Verifica que API_GATEWAY_URL esté configurada en Vercel
# Settings → Environment Variables

# Debe ser la URL de Azure, ejemplo:
API_GATEWAY_URL=http://linktic-ecommerce-gateway.eastus.azurecontainer.io:8080
```

### Container en Azure no arranca

```bash
# Ver logs
az container logs --resource-group rg-linktic-ecommerce --name [NOMBRE_SERVICIO]

# Reiniciar
az container restart --resource-group rg-linktic-ecommerce --name [NOMBRE_SERVICIO]
```

### Build de Vercel falla

```bash
# Asegúrate de que vercel.json esté en frontend_angular/
cp deployment/vercel/vercel.json frontend_angular/
git add frontend_angular/vercel.json
git commit -m "fix: Add vercel.json"
git push
```

---

## 📞 Soporte

- **Documentación completa:** `deployment/README.md`
- **Configuración Vercel:** `deployment/vercel/README-VERCEL.md`
- **Scripts SQL:** `deployment/supabase/*.sql`
- **Azure script:** `deployment/azure/deploy-to-azure.sh`

---

## ✅ Checklist Mínimo

- [ ] 3 proyectos en Supabase creados y con datos
- [ ] `.env.azure` configurado
- [ ] Script de Azure ejecutado
- [ ] API Gateway accesible (curl funcionando)
- [ ] Vercel desplegado con variable API_GATEWAY_URL
- [ ] Frontend carga productos correctamente
- [ ] Compra de prueba funciona

**¡Listo en ~1 hora!** 🎉
