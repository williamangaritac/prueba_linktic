# Stage 1: Build Angular app
FROM node:18-alpine AS builder

WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar el código fuente
COPY . .

# Build de producción
RUN npm run build --prod

# Stage 2: Serve con Nginx
FROM nginx:alpine

# Copiar archivos compilados desde el subdirectorio browser
COPY --from=builder /app/dist/frontend-angular/browser /usr/share/nginx/html/

# Copiar configuración de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto 80
EXPOSE 80

# Comando para iniciar nginx
CMD ["nginx", "-g", "daemon off;"]
