// Environment configuration for production
// All requests go through API Gateway (port 8080) which routes to microservices via Eureka
export const environment = {
  production: true,
  apiUrl: 'http://localhost:8080/api/v1',  // API Gateway → Products Service
  inventoryApiUrl: 'http://localhost:8080/api/v1/inventory'  // API Gateway → Inventory Service
};

