import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { Product, ProductRequest, Page } from '../models/product.model';

/**
 * Product Repository Interface
 * Defines contract for product data access
 */
export interface IProductRepository {
  /**
   * Get paginated products (6 per page)
   * @param page Page number (0-based)
   */
  getProducts(page: number): Observable<Page<Product>>;

  /**
   * Get product details by SKU
   * @param sku Product SKU
   */
  getProductBySku(sku: string): Observable<Product>;

  /**
   * Get all active products
   * @param page Page number (0-based)
   */
  getActiveProducts(page: number): Observable<Page<Product>>;

  /**
   * Create a new product
   * @param productRequest Product data to create
   */
  createProduct(productRequest: ProductRequest): Observable<Product>;
}

export const PRODUCT_REPOSITORY_TOKEN = new InjectionToken<IProductRepository>(
  'IProductRepository'
);
