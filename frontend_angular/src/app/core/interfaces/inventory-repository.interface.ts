import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { Inventory } from '../models/product.model';

/**
 * Inventory Repository Interface
 * Defines contract for inventory data access
 */
export interface IInventoryRepository {
  /**
   * Get inventory (stock) by SKU
   * @param sku Product SKU
   */
  getInventoryBySku(sku: string): Observable<Inventory>;
  
  /**
   * Get all inventory items
   */
  getAllInventory(): Observable<Inventory[]>;
}

export const INVENTORY_REPOSITORY_TOKEN = new InjectionToken<IInventoryRepository>(
  'IInventoryRepository'
);

