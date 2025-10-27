import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, Page } from '../../core/models/product.model';
import { IProductRepository } from '../../core/interfaces/product-repository.interface';
import { environment } from '../../../environments/environment';

/**
 * Product Repository Implementation
 * Connects to Products Service backend via API Gateway
 */
@Injectable({
  providedIn: 'root'
})
export class ProductRepository implements IProductRepository {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/products`;

  /**
   * Get paginated products (6 per page)
   * GET /api/v1/products?page=0&size=6
   */
  getProducts(page: number = 0): Observable<Page<Product>> {
    return this.http.get<Page<Product>>(`${this.apiUrl}?page=${page}&size=6`);
  }

  /**
   * Get product details by SKU
   * GET /api/v1/products/sku/{sku}
   */
  getProductBySku(sku: string): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/sku/${sku}`);
  }

  /**
   * Get only active products (6 per page)
   * GET /api/v1/products/active?page=0&size=6
   */
  getActiveProducts(page: number = 0): Observable<Page<Product>> {
    return this.http.get<Page<Product>>(`${this.apiUrl}/active?page=${page}&size=6`);
  }
}

