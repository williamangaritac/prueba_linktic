import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IOrderRepository } from '../../core/interfaces/order-repository.interface';
import { OrderRequest, PurchaseResponse } from '../../core/models/order.model';
import { environment } from '../../../environments/environment';

/**
 * Order Repository Implementation
 * Handles HTTP requests to Orders Service through API Gateway
 */
@Injectable({
  providedIn: 'root'
})
export class OrderRepository implements IOrderRepository {
  private readonly apiUrl = `${environment.apiUrl}/frontend/orders`;

  constructor(private http: HttpClient) {}

  /**
   * Create a new order (purchase)
   * POST /api/v1/frontend/orders/purchase
   */
  createOrder(orderRequest: OrderRequest): Observable<PurchaseResponse> {
    console.log('Creating order:', orderRequest);
    return this.http.post<PurchaseResponse>(`${this.apiUrl}/purchase`, orderRequest);
  }
}

