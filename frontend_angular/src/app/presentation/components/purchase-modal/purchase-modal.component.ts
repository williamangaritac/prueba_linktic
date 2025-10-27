import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product } from '../../../core/models/product.model';

/**
 * Purchase Modal Component
 * Modal for purchasing a product with quantity selection
 */
@Component({
  selector: 'app-purchase-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase-modal.component.html',
  styleUrls: ['./purchase-modal.component.css']
})
export class PurchaseModalComponent implements OnInit {
  @Input() product: Product | null = null;
  @Input() isOpen: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() confirmPurchase = new EventEmitter<{ product: Product, quantity: number }>();

  quantity: number = 1;
  isProcessing: boolean = false;

  ngOnInit(): void {
    // Reset quantity when modal opens
    if (this.isOpen) {
      this.quantity = 1;
    }
  }

  /**
   * Get total price based on quantity
   */
  get totalPrice(): number {
    if (!this.product) return 0;
    return this.product.price * this.quantity;
  }

  /**
   * Get formatted unit price
   */
  get formattedUnitPrice(): string {
    if (!this.product) return '';
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(this.product.price);
  }

  /**
   * Get formatted total price
   */
  get formattedTotalPrice(): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(this.totalPrice);
  }

  /**
   * Get maximum quantity available
   */
  get maxQuantity(): number {
    return this.product?.stock || 0;
  }

  /**
   * Check if purchase button should be disabled
   */
  get isPurchaseDisabled(): boolean {
    return this.quantity < 1 || 
           this.quantity > this.maxQuantity || 
           this.isProcessing ||
           this.maxQuantity === 0;
  }

  /**
   * Increase quantity
   */
  increaseQuantity(): void {
    if (this.quantity < this.maxQuantity) {
      this.quantity++;
    }
  }

  /**
   * Decrease quantity
   */
  decreaseQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  /**
   * Handle quantity input change
   */
  onQuantityChange(): void {
    // Ensure quantity is within valid range
    if (this.quantity < 1) {
      this.quantity = 1;
    } else if (this.quantity > this.maxQuantity) {
      this.quantity = this.maxQuantity;
    }
  }

  /**
   * Close modal
   */
  onClose(): void {
    if (!this.isProcessing) {
      this.quantity = 1;
      this.close.emit();
    }
  }

  /**
   * Handle backdrop click
   */
  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }

  /**
   * Confirm purchase
   */
  onConfirmPurchase(): void {
    if (this.product && !this.isPurchaseDisabled) {
      this.isProcessing = true;
      this.confirmPurchase.emit({
        product: this.product,
        quantity: this.quantity
      });
    }
  }

  /**
   * Reset processing state (called from parent after purchase completes)
   */
  resetProcessing(): void {
    this.isProcessing = false;
  }
}

