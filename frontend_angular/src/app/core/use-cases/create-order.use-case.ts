import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs';
import { IOrderRepository, ORDER_REPOSITORY_TOKEN } from '../interfaces/order-repository.interface';
import { OrderRequest, PurchaseResponse } from '../models/order.model';

/**
 * Create Order Use Case
 * Business logic for creating orders
 */
@Injectable({
  providedIn: 'root'
})
export class CreateOrderUseCase {
  constructor(
    @Inject(ORDER_REPOSITORY_TOKEN) private orderRepository: IOrderRepository
  ) {}

  /**
   * Execute order creation
   * @param orderRequest Order request with items
   * @returns Observable with purchase response
   */
  execute(orderRequest: OrderRequest): Observable<PurchaseResponse> {
    return this.orderRepository.createOrder(orderRequest);
  }
}

