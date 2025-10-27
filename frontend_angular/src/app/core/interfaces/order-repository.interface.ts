import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { OrderRequest, PurchaseResponse } from '../models/order.model';

/**
 * Order Repository Interface
 * Defines contract for order data operations
 */
export interface IOrderRepository {
  /**
   * Create a new order (purchase)
   * @param orderRequest Order request with items
   * @returns Observable with purchase response
   */
  createOrder(orderRequest: OrderRequest): Observable<PurchaseResponse>;
}

/**
 * Injection token for Order Repository
 */
export const ORDER_REPOSITORY_TOKEN = new InjectionToken<IOrderRepository>('OrderRepository');

