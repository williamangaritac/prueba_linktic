import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { PRODUCT_REPOSITORY_TOKEN } from '../core/interfaces/product-repository.interface';
import { INVENTORY_REPOSITORY_TOKEN } from '../core/interfaces/inventory-repository.interface';
import { ORDER_REPOSITORY_TOKEN } from '../core/interfaces/order-repository.interface';
import { ProductRepository } from '../infrastructure/repositories/product.repository';
import { InventoryRepository } from '../infrastructure/repositories/inventory.repository';
import { OrderRepository } from '../infrastructure/repositories/order.repository';

/**
 * Application Configuration
 * Provides HTTP client and repository implementations
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter([]),
    provideHttpClient(), // Enable HTTP client for API calls
    {
      provide: PRODUCT_REPOSITORY_TOKEN,
      useClass: ProductRepository
    },
    {
      provide: INVENTORY_REPOSITORY_TOKEN,
      useClass: InventoryRepository
    },
    {
      provide: ORDER_REPOSITORY_TOKEN,
      useClass: OrderRepository
    }
  ]
};

