// Environment configuration for production (Vercel)
// This file should replace src/environments/environment.ts during build

declare const process: any;

export const environment = {
  production: true,
  // En Vercel, las variables de entorno se inyectan en tiempo de build
  // La configuración de vercel.json hace el proxy de /api/v1 al API Gateway
  apiUrl: '/api/v1',
  inventoryApiUrl: '/api/v1/inventory'
};
