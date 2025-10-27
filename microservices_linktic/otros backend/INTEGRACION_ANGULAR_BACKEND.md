# 🎨 Integración Angular Frontend con Backend Spring Boot

## 📋 Guía Completa para Desarrolladores Frontend

---

## 🎯 Arquitectura de Integración

```
Angular (4200) → API Gateway (8080) → Microservicios (8081-8084)
```

**Ventajas:**
- ✅ Punto de entrada único (API Gateway)
- ✅ CORS configurado automáticamente
- ✅ Circuit Breaker para resiliencia
- ✅ Load Balancing automático
- ✅ Service Discovery transparente

---

## 🔧 Configuración en Angular

### 1. Environment Configuration

**`src/environments/environment.ts`:**
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

**`src/environments/environment.prod.ts`:**
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.linktic.com/api/v1'  // Producción
};
```

---

## 📦 Modelos TypeScript

### Product Model
```typescript
export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  price: number;
  status: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
```

### Order Models
```typescript
export interface OrderItem {
  sku: string;
  price: number;
  quantity: number;
}

export interface OrderRequest {
  orderItems: OrderItem[];
}

export interface OrderResponse {
  id: number;
  orderNumber: string;
  orderItems: OrderItemResponse[];
}

export interface OrderItemResponse {
  id: number;
  sku: string;
  price: number;
  quantity: number;
}

export interface PurchaseResponse {
  success: boolean;
  message: string;
  order: OrderResponse;
  orderNumber: string;
  notification: string;
}
```

---

## 🔌 Services Angular

### Product Service

**`src/app/services/product.service.ts`:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Product, Page } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/frontend/products`;

  constructor(private http: HttpClient) {}

  /**
   * Obtener productos paginados (6 por página)
   * Para mostrar en el catálogo principal
   */
  getProducts(page: number = 0): Observable<Page<Product>> {
    return this.http.get<Page<Product>>(`${this.apiUrl}?page=${page}`);
  }

  /**
   * Obtener solo productos activos
   */
  getActiveProducts(page: number = 0): Observable<Page<Product>> {
    return this.http.get<Page<Product>>(`${this.apiUrl}/active?page=${page}`);
  }

  /**
   * Obtener detalles completos de un producto
   * Para el botón "Ver Detalles"
   */
  getProductDetails(sku: string): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/sku/${sku}`);
  }
}
```

### Order Service

**`src/app/services/order.service.ts`:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { OrderRequest, PurchaseResponse } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/frontend/orders`;

  constructor(private http: HttpClient) {}

  /**
   * Crear orden de compra
   * Para el botón "Comprar"
   */
  purchase(orderRequest: OrderRequest): Observable<PurchaseResponse> {
    return this.http.post<PurchaseResponse>(
      `${this.apiUrl}/purchase`,
      orderRequest
    );
  }

  /**
   * Validar disponibilidad antes de comprar
   */
  validateOrder(orderRequest: OrderRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/validate`, orderRequest);
  }
}
```

---

## 🎨 Componentes Angular

### Product List Component

**`src/app/components/product-list/product-list.component.ts`:**
```typescript
import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { Product, Page } from '../../models/product.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  currentPage: number = 0;
  totalPages: number = 0;
  totalElements: number = 0;
  loading: boolean = false;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getActiveProducts(this.currentPage).subscribe({
      next: (page: Page<Product>) => {
        this.products = page.content;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.loading = false;
      }
    });
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadProducts();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProducts();
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadProducts();
  }

  viewDetails(sku: string): void {
    this.productService.getProductDetails(sku).subscribe({
      next: (product: Product) => {
        // Mostrar modal o navegar a página de detalles
        console.log('Product details:', product);
      },
      error: (error) => {
        console.error('Error loading product details:', error);
      }
    });
  }

  addToCart(product: Product): void {
    // Agregar al carrito (implementar según tu lógica)
    console.log('Adding to cart:', product);
  }
}
```

**`src/app/components/product-list/product-list.component.html`:**
```html
<div class="product-list-container">
  <h2>Catálogo de Productos</h2>
  
  <!-- Loading Spinner -->
  <div *ngIf="loading" class="loading-spinner">
    <p>Cargando productos...</p>
  </div>

  <!-- Product Grid (6 productos por página) -->
  <div *ngIf="!loading" class="product-grid">
    <div *ngFor="let product of products" class="product-card">
      <div class="product-image">
        <!-- Imagen del producto -->
      </div>
      
      <div class="product-info">
        <h3>{{ product.name }}</h3>
        <p class="product-description">{{ product.description }}</p>
        <p class="product-price">${{ product.price | number:'1.2-2' }}</p>
        <p class="product-sku">SKU: {{ product.sku }}</p>
      </div>
      
      <div class="product-actions">
        <button 
          class="btn btn-primary" 
          (click)="viewDetails(product.sku)">
          Ver Detalles
        </button>
        <button 
          class="btn btn-success" 
          (click)="addToCart(product)">
          Comprar
        </button>
      </div>
    </div>
  </div>

  <!-- Pagination -->
  <div *ngIf="!loading && totalPages > 1" class="pagination">
    <button 
      [disabled]="currentPage === 0" 
      (click)="previousPage()">
      Anterior
    </button>
    
    <span class="page-info">
      Página {{ currentPage + 1 }} de {{ totalPages }}
      ({{ totalElements }} productos)
    </span>
    
    <button 
      [disabled]="currentPage === totalPages - 1" 
      (click)="nextPage()">
      Siguiente
    </button>
  </div>
</div>
```

### Shopping Cart Component

**`src/app/components/shopping-cart/shopping-cart.component.ts`:**
```typescript
import { Component } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { OrderRequest, OrderItem, PurchaseResponse } from '../../models/order.model';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css']
})
export class ShoppingCartComponent {
  cartItems: OrderItem[] = [];
  purchasing: boolean = false;
  purchaseSuccess: boolean = false;
  purchaseMessage: string = '';

  constructor(private orderService: OrderService) {}

  addItem(sku: string, price: number, quantity: number = 1): void {
    const existingItem = this.cartItems.find(item => item.sku === sku);
    
    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      this.cartItems.push({ sku, price, quantity });
    }
  }

  removeItem(sku: string): void {
    this.cartItems = this.cartItems.filter(item => item.sku !== sku);
  }

  getTotalAmount(): number {
    return this.cartItems.reduce((total, item) => {
      return total + (item.price * item.quantity);
    }, 0);
  }

  /**
   * Botón "Comprar" - Crea la orden
   * Flujo:
   * 1. Valida inventario
   * 2. Crea orden en MySQL
   * 3. Publica evento en Kafka
   * 4. Envía email de confirmación
   */
  purchase(): void {
    if (this.cartItems.length === 0) {
      alert('El carrito está vacío');
      return;
    }

    this.purchasing = true;
    
    const orderRequest: OrderRequest = {
      orderItems: this.cartItems
    };

    this.orderService.purchase(orderRequest).subscribe({
      next: (response: PurchaseResponse) => {
        this.purchasing = false;
        this.purchaseSuccess = true;
        this.purchaseMessage = response.message;
        
        // Mostrar mensaje de éxito
        alert(`${response.message}\n\nNúmero de Orden: ${response.orderNumber}\n\n${response.notification}`);
        
        // Limpiar carrito
        this.cartItems = [];
      },
      error: (error) => {
        this.purchasing = false;
        this.purchaseSuccess = false;
        
        const errorMessage = error.error?.message || 'Error al procesar la compra';
        alert(`Error: ${errorMessage}`);
        
        console.error('Purchase error:', error);
      }
    });
  }
}
```

---

## 🔒 HTTP Interceptor (Opcional)

Para agregar headers automáticamente:

**`src/app/interceptors/api.interceptor.ts`:**
```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Agregar headers comunes
    const clonedRequest = req.clone({
      setHeaders: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    return next.handle(clonedRequest);
  }
}
```

**Registrar en `app.module.ts`:**
```typescript
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ApiInterceptor } from './interceptors/api.interceptor';

@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ApiInterceptor,
      multi: true
    }
  ]
})
export class AppModule { }
```

---

## 🧪 Testing

### Product Service Test

```typescript
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductService } from './product.service';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService]
    });
    
    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should fetch products', () => {
    const mockPage = {
      content: [],
      totalPages: 3,
      totalElements: 15,
      number: 0,
      size: 6
    };

    service.getProducts(0).subscribe(page => {
      expect(page.totalElements).toBe(15);
      expect(page.size).toBe(6);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/v1/frontend/products?page=0');
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });
});
```

---

## 📊 Resumen de Endpoints

| Funcionalidad | Método | Endpoint | Descripción |
|---------------|--------|----------|-------------|
| **Listar Productos** | GET | `/api/v1/frontend/products?page=0` | 6 productos por página |
| **Productos Activos** | GET | `/api/v1/frontend/products/active?page=0` | Solo productos activos |
| **Ver Detalles** | GET | `/api/v1/frontend/products/sku/{sku}` | Detalles completos |
| **Comprar** | POST | `/api/v1/frontend/orders/purchase` | Crear orden + Kafka + Email |
| **Validar** | POST | `/api/v1/frontend/orders/validate` | Validar disponibilidad |

---

## ✅ Checklist de Integración

- [ ] Configurar `environment.ts` con `apiUrl`
- [ ] Crear modelos TypeScript (Product, Order, etc.)
- [ ] Implementar ProductService
- [ ] Implementar OrderService
- [ ] Crear ProductListComponent (6 productos por página)
- [ ] Crear ShoppingCartComponent
- [ ] Implementar botón "Ver Detalles"
- [ ] Implementar botón "Comprar"
- [ ] Agregar manejo de errores
- [ ] Agregar loading spinners
- [ ] Probar paginación (3 páginas con 15 productos)
- [ ] Probar flujo completo de compra

---

**¡Listo para integrar! 🚀**

