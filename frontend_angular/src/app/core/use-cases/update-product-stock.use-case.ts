import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

/**
 * Use Case: Update Product Stock
 * NOTE: Stock updates are now handled by the backend (Inventory Service)
 * This use case is kept for compatibility but returns a mock success response
 */
@Injectable({
  providedIn: 'root'
})
export class UpdateProductStockUseCase {
  /**
   * Execute stock update
   * @param productId Product ID
   * @param quantity Quantity to update
   * @returns Observable<boolean> - Always returns true (mock)
   */
  execute(productId: number, quantity: number = 1): Observable<boolean> {
    // Stock updates should be handled by calling Orders Service
    // which will then update Inventory Service
    console.warn('UpdateProductStockUseCase: Stock updates should be handled by backend');
    return of(true);
  }
}
