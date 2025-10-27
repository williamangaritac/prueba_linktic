import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Product, Page } from '../models/product.model';
import { IProductRepository, PRODUCT_REPOSITORY_TOKEN } from '../interfaces/product-repository.interface';

/**
 * Use Case: Get Products
 * Retrieves paginated products from the backend
 */
@Injectable({
  providedIn: 'root'
})
export class GetProductsUseCase {
  constructor(
    @Inject(PRODUCT_REPOSITORY_TOKEN) private productRepository: IProductRepository
  ) {}

  /**
   * Execute use case to get products
   * @param page Page number (0-based)
   */
  execute(page: number = 0): Observable<Page<Product>> {
    return this.productRepository.getProducts(page);
  }

  /**
   * Get product details by SKU
   * @param sku Product SKU
   */
  getProductBySku(sku: string): Observable<Product> {
    return this.productRepository.getProductBySku(sku);
  }
}
