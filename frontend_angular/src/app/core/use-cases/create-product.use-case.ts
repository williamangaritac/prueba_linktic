import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Product, ProductRequest } from '../models/product.model';
import { IProductRepository, PRODUCT_REPOSITORY_TOKEN } from '../interfaces/product-repository.interface';

/**
 * Create Product Use Case
 * Handles the business logic for creating a new product
 */
@Injectable({
  providedIn: 'root'
})
export class CreateProductUseCase {
  constructor(
    @Inject(PRODUCT_REPOSITORY_TOKEN) private productRepository: IProductRepository
  ) {}

  /**
   * Execute the create product use case
   * @param productRequest Product data to create
   * @returns Observable of the created product
   */
  execute(productRequest: ProductRequest): Observable<Product> {
    return this.productRepository.createProduct(productRequest);
  }
}

