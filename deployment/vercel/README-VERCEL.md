# 🚀 Despliegue del Frontend en Vercel

Esta guía te ayudará a desplegar el frontend Angular en Vercel.

## 📋 Pre-requisitos

1. Cuenta gratuita en Vercel (https://vercel.com)
2. Tener desplegado el backend en Azure y obtener la URL del API Gateway
3. Código del frontend en un repositorio Git (GitHub, GitLab, Bitbucket)

## 🔧 Opción 1: Despliegue desde la Interfaz Web de Vercel (Recomendado)

### Paso 1: Conectar Repositorio

1. Ve a https://vercel.com y haz login
2. Click en **"Add New..."** → **"Project"**
3. Importa tu repositorio Git
4. Selecciona el repositorio `prueba_linktic`

### Paso 2: Configurar el Proyecto

En la configuración del proyecto:

**Framework Preset:**
- Selecciona: `Angular`

**Root Directory:**
- Deja en blanco o especifica: `frontend_angular`

**Build Command:**
```bash
npm run build
```

**Output Directory:**
```bash
dist/frontend-angular/browser
```

**Install Command:**
```bash
npm ci
```

### Paso 3: Variables de Entorno

En la sección **"Environment Variables"**, agrega:

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `API_GATEWAY_URL` | `http://[TU_AZURE_GATEWAY]:8080` | URL del API Gateway desplegado en Azure |

**Ejemplo:**
```
API_GATEWAY_URL=http://linktic-ecommerce-gateway.eastus.azurecontainer.io:8080
```

### Paso 4: Copiar vercel.json

Antes de desplegar, copia el archivo `vercel.json` a la raíz del proyecto frontend:

```bash
cp deployment/vercel/vercel.json frontend_angular/
```

**O copia este contenido manualmente:**

```json
{
  "version": 2,
  "buildCommand": "npm run build",
  "outputDirectory": "dist/frontend-angular/browser",
  "framework": "angular",
  "rewrites": [
    {
      "source": "/api/v1/:path*",
      "destination": "$API_GATEWAY_URL/api/v1/:path*"
    }
  ],
  "routes": [
    {
      "src": "/(.*)",
      "dest": "/index.html"
    }
  ]
}
```

### Paso 5: Desplegar

1. Click en **"Deploy"**
2. Vercel construirá y desplegará automáticamente
3. Obtendrás una URL como: `https://tu-proyecto.vercel.app`

## 🔧 Opción 2: Despliegue con Vercel CLI

### Instalación

```bash
npm install -g vercel
```

### Login

```bash
vercel login
```

### Desplegar

Desde la carpeta `frontend_angular`:

```bash
cd frontend_angular

# Primera vez (configuración interactiva)
vercel

# Despliegues posteriores
vercel --prod
```

Durante la configuración interactiva:
- Set up and deploy?: **Y**
- Which scope?: Selecciona tu cuenta
- Link to existing project?: **N**
- What's your project's name?: `linktic-ecommerce-frontend`
- In which directory is your code located?: `./`
- Want to override the settings?: **Y**
  - Build Command: `npm run build`
  - Output Directory: `dist/frontend-angular/browser`
  - Development Command: `npm start`

### Configurar Variables de Entorno (CLI)

```bash
# Agregar variable de entorno
vercel env add API_GATEWAY_URL

# Cuando pregunte por el valor, ingresa:
# http://[TU_AZURE_GATEWAY]:8080

# Selecciona todos los entornos: Production, Preview, Development
```

## 🌐 Configuración de Dominio (Opcional)

### Dominio Personalizado

1. En el dashboard de Vercel, ve a tu proyecto
2. Click en **"Settings"** → **"Domains"**
3. Agrega tu dominio personalizado
4. Sigue las instrucciones para configurar los DNS

## 🔍 Verificación del Despliegue

### 1. Verificar que el sitio esté activo

Visita tu URL de Vercel: `https://tu-proyecto.vercel.app`

### 2. Verificar conexión con el backend

Abre las Developer Tools del navegador (F12) y verifica:

```javascript
// En la consola del navegador
fetch('/api/v1/products')
  .then(r => r.json())
  .then(console.log)
```

Deberías ver los productos retornados desde el backend.

### 3. Verificar rutas

- Página principal: `https://tu-proyecto.vercel.app/`
- Catálogo: `https://tu-proyecto.vercel.app/catalog`

## 🐛 Troubleshooting

### Error: "Cannot GET /api/v1/products"

**Causa:** La variable `API_GATEWAY_URL` no está configurada o es incorrecta.

**Solución:**
1. Ve a Settings → Environment Variables en Vercel
2. Verifica que `API_GATEWAY_URL` esté configurada correctamente
3. Re-deploya el proyecto

### Error: CORS

**Causa:** El backend no permite peticiones desde tu dominio de Vercel.

**Solución:**
Agrega el dominio de Vercel a las configuraciones CORS del API Gateway:

```yaml
# En api-gateway/src/main/resources/application.yml
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "https://tu-proyecto.vercel.app"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders: "*"
            allowCredentials: true
```

### Build Fallido

**Causa:** Dependencias faltantes o error en el código.

**Solución:**
1. Revisa los logs en el dashboard de Vercel
2. Asegúrate de que `package.json` y `package-lock.json` estén actualizados
3. Verifica que el build funcione localmente:
   ```bash
   npm ci
   npm run build
   ```

## 📊 Monitoreo

Vercel proporciona:
- **Analytics**: Tráfico y rendimiento
- **Logs**: Logs en tiempo real de las funciones serverless
- **Speed Insights**: Métricas de rendimiento Core Web Vitals

Accede desde: Dashboard → Tu Proyecto → Analytics/Logs

## 💰 Límites del Plan Gratuito (Hobby)

| Recurso | Límite |
|---------|--------|
| Despliegues | Ilimitados |
| Ancho de banda | 100 GB/mes |
| Builds | 6,000 min/mes |
| Funciones Serverless | 100 GB-Hrs |
| Imágenes Optimizadas | 1,000 imágenes/mes |

**Para 2 días de prueba:** ✅ Más que suficiente

## 🔄 Re-despliegues Automáticos

Vercel re-despliega automáticamente cuando:
- Haces push a la rama principal (main/master)
- Haces push a cualquier rama (crea un preview deployment)

Para desactivar:
Settings → Git → Production Branch → Desactiva auto-deploys

## 🗑️ Eliminar Proyecto

Cuando termines la prueba de 2 días:

1. Ve al dashboard de Vercel
2. Selecciona tu proyecto
3. Settings → General → Delete Project
4. Confirma escribiendo el nombre del proyecto

## 📚 Recursos Adicionales

- [Documentación de Vercel](https://vercel.com/docs)
- [Desplegar Angular en Vercel](https://vercel.com/guides/deploying-angular-with-vercel)
- [Variables de Entorno](https://vercel.com/docs/concepts/projects/environment-variables)

---

## 🎯 Comandos Rápidos

```bash
# Desplegar desde CLI
cd frontend_angular
vercel --prod

# Ver logs en tiempo real
vercel logs

# Ver información del proyecto
vercel inspect

# Eliminar proyecto
vercel remove linktic-ecommerce-frontend
```

## ✅ Checklist de Despliegue

- [ ] Backend desplegado en Azure y funcionando
- [ ] URL del API Gateway obtenida
- [ ] `vercel.json` copiado a `frontend_angular/`
- [ ] Código subido a Git repository
- [ ] Proyecto creado en Vercel
- [ ] Variable `API_GATEWAY_URL` configurada
- [ ] Build exitoso en Vercel
- [ ] Sitio accesible desde la URL de Vercel
- [ ] Conexión con backend verificada
- [ ] Funcionalidad de catálogo y compras probada

---

**¡Listo!** Tu frontend Angular está desplegado en Vercel y conectado al backend en Azure. 🎉
