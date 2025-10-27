import { Component, OnInit, OnDestroy, Inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil, forkJoin } from 'rxjs';
import { Product } from '../../../core/models/product.model';
import { OrderRequest } from '../../../core/models/order.model';
import { GetProductsUseCase } from '../../../core/use-cases/get-products.use-case';
import { UpdateProductStockUseCase } from '../../../core/use-cases/update-product-stock.use-case';
import { CreateOrderUseCase } from '../../../core/use-cases/create-order.use-case';
import { IInventoryRepository, INVENTORY_REPOSITORY_TOKEN } from '../../../core/interfaces/inventory-repository.interface';
import { ProductCardComponent } from '../../components/product-card/product-card.component';
import { PaginationComponent } from '../../components/pagination/pagination.component';
import { ProductModalComponent } from '../../components/product-modal/product-modal.component';
import { PurchaseModalComponent } from '../../components/purchase-modal/purchase-modal.component';

/**
 * Catalog Component
 * Displays paginated products from backend with stock from Inventory Service
 */
@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [
    CommonModule,
    ProductCardComponent,
    PaginationComponent,
    ProductModalComponent,
    PurchaseModalComponent
  ],
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent implements OnInit, OnDestroy {
  products: Product[] = [];
  selectedProduct: Product | null = null;
  isModalOpen: boolean = false;
  isPurchaseModalOpen: boolean = false;
  productToPurchase: Product | null = null;
  loading: boolean = false;

  // Pagination from backend
  currentPage: number = 0; // Backend uses 0-based pagination
  totalPages: number = 0;
  totalElements: number = 0;

  @ViewChild(PurchaseModalComponent) purchaseModal?: PurchaseModalComponent;

  private destroy$ = new Subject<void>();

  constructor(
    private getProductsUseCase: GetProductsUseCase,
    private updateProductStockUseCase: UpdateProductStockUseCase,
    private createOrderUseCase: CreateOrderUseCase,
    @Inject(INVENTORY_REPOSITORY_TOKEN) private inventoryRepository: IInventoryRepository
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Load products from backend (6 per page) and enrich with stock from Inventory Service
   */
  private loadProducts(): void {
    this.loading = true;
    this.getProductsUseCase
      .execute(this.currentPage)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (page) => {
          this.products = page.content;
          this.totalPages = page.totalPages;
          this.totalElements = page.totalElements;

          // Load stock for each product
          this.loadStockForProducts();

          console.log(`Loaded ${page.content.length} products from backend`);
        },
        error: (error) => {
          console.error('Error al cargar productos:', error);
          this.loading = false;
          alert('Error al cargar productos. Verifica que el backend esté corriendo.');
        }
      });
  }

  /**
   * Load stock for all products from Inventory Service
   */
  private loadStockForProducts(): void {
    if (this.products.length === 0) {
      this.loading = false;
      return;
    }

    // Create array of inventory requests for each product
    const inventoryRequests = this.products.map(product =>
      this.inventoryRepository.getInventoryBySku(product.sku)
    );

    // Execute all requests in parallel
    forkJoin(inventoryRequests)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (inventories) => {
          // Enrich products with stock (map quantity from backend to stock in frontend)
          this.products.forEach((product, index) => {
            product.stock = inventories[index]?.quantity || 0;
          });
          this.loading = false;
          console.log('Stock loaded for all products');
        },
        error: (error) => {
          console.error('Error al cargar stock:', error);
          // Set stock to 0 for all products if inventory service fails
          this.products.forEach(product => product.stock = 0);
          this.loading = false;
          console.warn('Inventory Service no disponible, mostrando stock como 0');
        }
      });
  }

  /**
   * Handle page change
   */
  onPageChange(page: number): void {
    this.currentPage = page - 1; // Convert to 0-based for backend
    this.loadProducts();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  /**
   * Handle view details click
   * Fetches product details from backend by SKU and enriches with stock
   */
  onViewDetails(product: Product): void {
    this.loading = true;
    console.log(`Fetching details for product SKU: ${product.sku}`);

    this.getProductsUseCase
      .getProductBySku(product.sku)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (productDetails) => {
          // Load stock for this product
          this.inventoryRepository.getInventoryBySku(productDetails.sku)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: (inventory) => {
                productDetails.stock = inventory.quantity; // Map quantity to stock
                this.selectedProduct = productDetails;
                this.isModalOpen = true;
                this.loading = false;
                console.log('Product details loaded with stock:', productDetails);
              },
              error: (error) => {
                console.error('Error al cargar stock del producto:', error);
                productDetails.stock = 0;
                this.selectedProduct = productDetails;
                this.isModalOpen = true;
                this.loading = false;
              }
            });
        },
        error: (error) => {
          console.error('Error al cargar detalles del producto:', error);
          this.loading = false;
          alert('Error al cargar detalles del producto');
        }
      });
  }

  onCloseModal(): void {
    this.isModalOpen = false;
    this.selectedProduct = null;
  }

  onPurchase(product: Product): void {
    console.log('Opening purchase modal for product:', product);
    this.productToPurchase = product;
    this.isPurchaseModalOpen = true;
  }

  onClosePurchaseModal(): void {
    this.isPurchaseModalOpen = false;
    this.productToPurchase = null;
  }

  onConfirmPurchase(event: { product: Product, quantity: number }): void {
    const { product, quantity } = event;

    console.log(`Purchasing ${quantity} units of ${product.name}`);

    // Create order request
    const orderRequest: OrderRequest = {
      customerEmail: 'william.angaritac@gmail.com', // Email del cliente
      orderItems: [
        {
          sku: product.sku,
          price: product.price,
          quantity: quantity
        }
      ]
    };

    // Call Orders Service through API Gateway
    this.createOrderUseCase.execute(orderRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('Order created successfully:', response);

          // Reset purchase modal processing state
          if (this.purchaseModal) {
            this.purchaseModal.resetProcessing();
          }

          // Close purchase modal
          this.onClosePurchaseModal();

          // Show success message with notification info
          const notificationMessage = `
✅ ¡Compra realizada exitosamente!

📦 Número de orden: ${response.orderNumber}
${response.message}

📧 NOTIFICACIÓN ENVIADA:
Se ha enviado un correo de confirmación a:
• william.angaritac@gmail.com
• contacto@linktic.com

El correo incluye los detalles completos de tu compra.
          `.trim();

          alert(notificationMessage);

          // Reload products to update stock
          this.loadProducts();
        },
        error: (error) => {
          console.error('Error creating order:', error);

          // Reset purchase modal processing state
          if (this.purchaseModal) {
            this.purchaseModal.resetProcessing();
          }

          // Show error message
          const errorMessage = error.error?.message || error.error?.error || 'Error al procesar la compra. Por favor, intenta de nuevo.';
          alert(`❌ Error en la compra\n\n${errorMessage}`);
        }
      });
  }

  /**
   * Get total pages for pagination component (1-based)
   */
  get totalPagesForPagination(): number {
    return this.totalPages;
  }

  /**
   * Get current page for pagination component (1-based)
   */
  get currentPageForPagination(): number {
    return this.currentPage + 1;
  }
}

