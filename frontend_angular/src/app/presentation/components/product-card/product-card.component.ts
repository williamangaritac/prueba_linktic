import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Product } from '../../../core/models/product.model';

/**
 * Product Card Component
 * Displays a product card with details and actions
 */
@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {
  @Input() product!: Product;
  @Output() viewDetails = new EventEmitter<Product>();
  @Output() purchase = new EventEmitter<Product>();

  /**
   * Get product status badge color
   */
  get statusBadgeClass(): string {
    return this.product.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800';
  }

  /**
   * Get product status text
   */
  get statusText(): string {
    return this.product.status ? 'Activo' : 'Inactivo';
  }

  /**
   * Check if stock is low (less than or equal to 10)
   */
  get isLowStock(): boolean {
    return (this.product.stock || 0) <= 10;
  }

  /**
   * Check if product is out of stock
   */
  get isOutOfStock(): boolean {
    return (this.product.stock || 0) === 0;
  }

  /**
   * Get stock badge class based on quantity
   */
  get stockBadgeClass(): string {
    if (this.isOutOfStock) {
      return 'text-red-600';
    } else if (this.isLowStock) {
      return 'text-orange-600';
    }
    return 'text-green-600';
  }

  /**
   * Get formatted price
   */
  get formattedPrice(): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(this.product.price);
  }

  /**
   * Get placeholder image
   */
  get productImage(): string {
    // Generate a placeholder image based on product ID
    const colors = ['3498db', '2ecc71', 'e74c3c', 'f39c12', '9b59b6', '1abc9c'];
    const color = colors[this.product.id % colors.length];
    return `https://placehold.co/600x400/${color}/ffffff?text=${encodeURIComponent(this.product.name.substring(0, 20))}`;
  }

  onViewDetails(): void {
    this.viewDetails.emit(this.product);
  }

  onPurchase(): void {
    if (this.product.status && !this.isOutOfStock) {
      this.purchase.emit(this.product);
    }
  }
}

