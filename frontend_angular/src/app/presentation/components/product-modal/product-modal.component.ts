import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Product } from '../../../core/models/product.model';

/**
 * Product Modal Component
 * Displays detailed product information in a modal
 */
@Component({
  selector: 'app-product-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-modal.component.html',
  styleUrls: ['./product-modal.component.css']
})
export class ProductModalComponent {
  @Input() product: Product | null = null;
  @Input() isOpen: boolean = false;
  @Output() close = new EventEmitter<void>();

  /**
   * Get formatted price
   */
  get formattedPrice(): string {
    if (!this.product) return '';
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(this.product.price);
  }

  /**
   * Get product status badge class
   */
  get statusBadgeClass(): string {
    if (!this.product) return '';
    return this.product.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800';
  }

  /**
   * Get product status text
   */
  get statusText(): string {
    if (!this.product) return '';
    return this.product.status ? 'Activo' : 'Inactivo';
  }

  /**
   * Check if stock is low (less than or equal to 10)
   */
  get isLowStock(): boolean {
    if (!this.product) return false;
    return (this.product.stock || 0) <= 10;
  }

  /**
   * Check if product is out of stock
   */
  get isOutOfStock(): boolean {
    if (!this.product) return false;
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
   * Get placeholder image
   */
  get productImage(): string {
    if (!this.product) return '';
    const colors = ['3498db', '2ecc71', 'e74c3c', 'f39c12', '9b59b6', '1abc9c'];
    const color = colors[this.product.id % colors.length];
    return `https://placehold.co/800x600/${color}/ffffff?text=${encodeURIComponent(this.product.name.substring(0, 30))}`;
  }

  /**
   * Format date
   */
  formatDate(dateString?: string): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  onClose(): void {
    this.close.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}

