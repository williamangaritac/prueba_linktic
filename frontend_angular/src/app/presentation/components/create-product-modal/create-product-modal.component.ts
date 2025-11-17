import { Component, EventEmitter, Output, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { CreateProductUseCase } from '../../../core/use-cases/create-product.use-case';
import { ProductRequest } from '../../../core/models/product.model';

/**
 * Create Product Modal Component
 * Modal form for creating new products with validation
 */
@Component({
  selector: 'app-create-product-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-product-modal.component.html',
  styleUrls: ['./create-product-modal.component.css']
})
export class CreateProductModalComponent implements OnDestroy {
  @Output() close = new EventEmitter<void>();
  @Output() productCreated = new EventEmitter<void>();

  productForm: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private createProductUseCase: CreateProductUseCase
  ) {
    this.productForm = this.fb.group({
      sku: ['', [Validators.required, Validators.maxLength(100)]],
      name: ['', [Validators.required, Validators.maxLength(255)]],
      description: ['', [Validators.maxLength(1000)]],
      price: ['', [Validators.required, Validators.min(0.01)]],
      status: [true, [Validators.required]]
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Handle form submission
   */
  onSubmit(): void {
    if (this.productForm.invalid) {
      this.markFormGroupTouched(this.productForm);
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const productRequest: ProductRequest = {
      sku: this.productForm.value.sku.trim(),
      name: this.productForm.value.name.trim(),
      description: this.productForm.value.description?.trim() || '',
      price: parseFloat(this.productForm.value.price),
      status: this.productForm.value.status
    };

    this.createProductUseCase
      .execute(productRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (product) => {
          console.log('Product created successfully:', product);
          this.isSubmitting = false;
          this.productCreated.emit();
          this.onClose();
        },
        error: (error) => {
          console.error('Error creating product:', error);
          this.isSubmitting = false;
          
          // Handle specific error messages
          if (error.status === 409) {
            this.errorMessage = `El producto con SKU "${productRequest.sku}" ya existe`;
          } else if (error.status === 400) {
            this.errorMessage = 'Datos inválidos. Por favor verifica los campos';
          } else {
            this.errorMessage = 'Error al crear el producto. Intenta nuevamente';
          }
        }
      });
  }

  /**
   * Close the modal
   */
  onClose(): void {
    this.productForm.reset({ status: true });
    this.errorMessage = '';
    this.close.emit();
  }

  /**
   * Mark all form fields as touched to show validation errors
   */
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Check if a field has errors and has been touched
   */
  hasError(fieldName: string): boolean {
    const field = this.productForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  /**
   * Get error message for a specific field
   */
  getErrorMessage(fieldName: string): string {
    const field = this.productForm.get(fieldName);
    if (!field || !field.errors) return '';

    if (field.errors['required']) return 'Este campo es requerido';
    if (field.errors['maxlength']) {
      const maxLength = field.errors['maxlength'].requiredLength;
      return `Máximo ${maxLength} caracteres`;
    }
    if (field.errors['min']) return 'El precio debe ser mayor a 0';

    return 'Campo inválido';
  }
}

