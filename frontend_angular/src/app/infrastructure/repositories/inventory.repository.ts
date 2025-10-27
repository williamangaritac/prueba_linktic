import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Inventory } from '../../core/models/product.model';
import { IInventoryRepository } from '../../core/interfaces/inventory-repository.interface';
import { environment } from '../../../environments/environment';

/**
 * Inventory Repository Implementation
 * Connects to Inventory Service backend via HTTP
 */
@Injectable({
  providedIn: 'root'
})
export class InventoryRepository implements IInventoryRepository {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.inventoryApiUrl;

  /**
   * Get inventory (stock) by SKU
   * GET /api/v1/inventory/{sku}
   */
  getInventoryBySku(sku: string): Observable<Inventory> {
    return this.http.get<Inventory>(`${this.apiUrl}/${sku}`);
  }

  /**
   * Get all inventory items
   * GET /api/v1/inventory
   */
  getAllInventory(): Observable<Inventory[]> {
    return this.http.get<Inventory[]>(this.apiUrl);
  }
}

