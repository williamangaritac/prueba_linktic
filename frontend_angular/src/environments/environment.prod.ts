// Environment configuration for production
// All requests go through nginx proxy to API Gateway which routes to microservices via Eureka
export const environment = {
  production: true,
  apiUrl: '/api/v1',  // Nginx proxy → API Gateway → Products Service
  inventoryApiUrl: '/api/v1/inventory',  // Nginx proxy → API Gateway → Inventory Service
  ordersApiUrl: '/api/v1'  // Nginx proxy → API Gateway → Orders Service
};

