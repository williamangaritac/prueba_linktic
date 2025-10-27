// Environment configuration for development
// All requests go through Nginx proxy which forwards to API Gateway
// Using relative URLs so they work both in development and Docker
export const environment = {
  production: false,
  apiUrl: '/api/v1',  // Nginx proxy → API Gateway → Products Service
  inventoryApiUrl: '/api/v1/inventory'  // Nginx proxy → API Gateway → Inventory Service
};

