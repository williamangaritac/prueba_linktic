// Environment configuration for development
// All requests go through nginx proxy to API Gateway
export const environment = {
  production: false,
  apiUrl: '/api/v1',  // Nginx proxy → API Gateway → Products Service
  inventoryApiUrl: '/api/v1/inventory',  // Nginx proxy → API Gateway → Inventory Service
  ordersApiUrl: '/api/v1'  // Nginx proxy → API Gateway → Orders Service
};

