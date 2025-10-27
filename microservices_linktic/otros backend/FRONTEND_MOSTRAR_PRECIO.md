# 💰 Guía para Mostrar Precio de Productos en el Frontend

## 🎯 Objetivo

Cambiar el botón "Consultar Precio" por mostrar directamente el **precio** de cada producto obtenido desde el backend (`products_service`).

---

## ✅ El Backend YA Envía el Precio

El endpoint de productos **YA incluye el precio** en cada respuesta:

### Endpoint: Listar Productos
```
GET http://localhost:8081/api/v1/frontend/products?page=0
```

### Respuesta del Backend:
```json
{
  "content": [
    {
      "id": 1,
      "sku": "29444ed7a8f8495587365a6b61458735",
      "name": "Solucion E-commerce",
      "description": "Plataforma completa de comercio electrónico",
      "price": 2805.00,           ← ✅ PRECIO INCLUIDO
      "status": true,
      "createdAt": "2025-10-25T10:30:00",
      "updatedAt": "2025-10-25T10:30:00"
    },
    {
      "id": 2,
      "sku": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
      "name": "Sistema de Gestión de Inventario",
      "description": "Control total de tu inventario en tiempo real",
      "price": 1850.00,           ← ✅ PRECIO INCLUIDO
      "status": true,
      "createdAt": "2025-10-25T10:30:00",
      "updatedAt": "2025-10-25T10:30:00"
    }
    // ... 4 productos más (6 total por página)
  ],
  "totalPages": 3,
  "totalElements": 15,
  "number": 0,
  "size": 6
}
```

---

## 🔧 Implementación en Angular

### 1. Modelo TypeScript (Ya debe estar creado)

**`src/app/models/product.model.ts`:**
```typescript
export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  price: number;              // ← Campo price
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

---

### 2. Servicio Angular (Ya debe estar creado)

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
   * El precio viene incluido en cada producto
   */
  getProducts(page: number = 0): Observable<Page<Product>> {
    return this.http.get<Page<Product>>(`${this.apiUrl}?page=${page}`);
  }

  /**
   * Obtener detalles completos de un producto
   * Incluye el precio
   */
  getProductDetails(sku: string): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/sku/${sku}`);
  }
}
```

---

### 3. Componente TypeScript

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
    this.productService.getProducts(this.currentPage).subscribe({
      next: (page: Page<Product>) => {
        this.products = page.content;  // Cada producto incluye price
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
}
```

---

### 4. Template HTML - **MOSTRAR PRECIO DIRECTAMENTE**

**`src/app/components/product-list/product-list.component.html`:**

#### ❌ **ANTES (Incorrecto - con botón "Consultar Precio"):**
```html
<div class="product-card" *ngFor="let product of products">
  <h3>{{ product.name }}</h3>
  <p>{{ product.description }}</p>
  
  <!-- ❌ NO HACER ESTO -->
  <button (click)="consultarPrecio(product.sku)">
    Consultar Precio
  </button>
  
  <button (click)="verDetalles(product.sku)">Ver Detalles</button>
  <button (click)="comprar(product)">Comprar</button>
</div>
```

#### ✅ **DESPUÉS (Correcto - mostrar precio directamente):**
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
      
      <!-- Imagen del producto (opcional) -->
      <div class="product-image">
        <img [src]="'assets/images/' + product.sku + '.jpg'" 
             [alt]="product.name"
             (error)="$event.target.src='assets/images/no-image.png'">
      </div>
      
      <!-- Información del producto -->
      <div class="product-info">
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-description">{{ product.description }}</p>
        
        <!-- ✅ MOSTRAR PRECIO DIRECTAMENTE -->
        <div class="product-price-container">
          <span class="price-label">Precio:</span>
          <span class="price-value">${{ product.price | number:'1.2-2' }}</span>
        </div>
        
        <p class="product-sku">SKU: {{ product.sku }}</p>
        
        <!-- Badge de estado (opcional) -->
        <span class="badge" [class.active]="product.status">
          {{ product.status ? 'Disponible' : 'No disponible' }}
        </span>
      </div>
      
      <!-- Botones de acción -->
      <div class="product-actions">
        <button 
          class="btn btn-primary" 
          (click)="viewDetails(product.sku)">
          Ver Detalles
        </button>
        <button 
          class="btn btn-success" 
          (click)="addToCart(product)"
          [disabled]="!product.status">
          Comprar
        </button>
      </div>
    </div>
  </div>

  <!-- Paginación -->
  <div *ngIf="!loading && totalPages > 1" class="pagination">
    <button 
      class="btn-pagination"
      [disabled]="currentPage === 0" 
      (click)="previousPage()">
      ← Anterior
    </button>
    
    <span class="page-info">
      Página {{ currentPage + 1 }} de {{ totalPages }}
      <br>
      <small>({{ totalElements }} productos en total)</small>
    </span>
    
    <button 
      class="btn-pagination"
      [disabled]="currentPage === totalPages - 1" 
      (click)="nextPage()">
      Siguiente →
    </button>
  </div>
</div>
```

---

### 5. Estilos CSS

**`src/app/components/product-list/product-list.component.css`:**
```css
.product-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.product-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 15px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  margin-bottom: 15px;
}

.product-name {
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.product-description {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 10px;
  line-height: 1.4;
}

/* ✅ ESTILOS PARA EL PRECIO */
.product-price-container {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 15px 0;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
}

.price-label {
  font-size: 0.9em;
  color: #666;
  font-weight: 500;
}

.price-value {
  font-size: 1.5em;
  font-weight: bold;
  color: #28a745;  /* Verde para el precio */
}

.product-sku {
  font-size: 0.8em;
  color: #999;
  margin: 5px 0;
}

.badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8em;
  font-weight: bold;
}

.badge.active {
  background: #d4edda;
  color: #155724;
}

.product-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.btn {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.2s;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover {
  background: #0056b3;
}

.btn-success {
  background: #28a745;
  color: white;
}

.btn-success:hover {
  background: #218838;
}

.btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.btn-pagination {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.btn-pagination:hover:not(:disabled) {
  background: #0056b3;
}

.btn-pagination:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.page-info {
  text-align: center;
  font-weight: bold;
}

.loading-spinner {
  text-align: center;
  padding: 40px;
  font-size: 1.2em;
  color: #666;
}
```

---

## 📊 Ejemplo Visual del Resultado

Cada tarjeta de producto mostrará:

```
┌─────────────────────────────────┐
│  [Imagen del Producto]          │
│                                 │
│  Solucion E-commerce            │
│  Plataforma completa de         │
│  comercio electrónico...        │
│                                 │
│  ┌───────────────────────────┐  │
│  │ Precio:  $2,805.00        │  │ ← ✅ PRECIO VISIBLE
│  └───────────────────────────┘  │
│                                 │
│  SKU: 29444ed7a8f8...           │
│  [Disponible]                   │
│                                 │
│  [Ver Detalles]  [Comprar]      │
└─────────────────────────────────┘
```

---

## 🔍 Verificar que el Precio se Muestra Correctamente

### 1. Abrir DevTools del Navegador (F12)

### 2. Ir a la pestaña "Network"

### 3. Recargar la página

### 4. Buscar la petición a `/frontend/products?page=0`

### 5. Verificar la respuesta JSON:
```json
{
  "content": [
    {
      "price": 2805.00  ← ✅ Debe estar presente
    }
  ]
}
```

### 6. En la consola del navegador, ejecutar:
```javascript
// Verificar que los productos tienen precio
console.log(this.products.map(p => ({ name: p.name, price: p.price })));
```

---

## ✅ Checklist de Implementación

- [ ] Verificar que el modelo `Product` tiene el campo `price: number`
- [ ] Verificar que el servicio llama a `http://localhost:8081/api/v1/frontend/products`
- [ ] **Eliminar** cualquier botón "Consultar Precio"
- [ ] **Agregar** `<span class="price-value">${{ product.price | number:'1.2-2' }}</span>` en el HTML
- [ ] Aplicar estilos CSS para que el precio se vea destacado
- [ ] Probar con las 3 páginas de productos (15 productos total)
- [ ] Verificar que el precio se muestra correctamente para todos los productos

---

## 🎯 Resumen

### ❌ **NO hacer esto:**
- Crear un botón "Consultar Precio"
- Hacer una petición adicional para obtener el precio
- Mostrar el precio solo al hacer clic

### ✅ **SÍ hacer esto:**
- Mostrar el precio directamente desde `product.price`
- Usar el pipe `number:'1.2-2'` para formatear el precio
- Mostrar el precio en todas las tarjetas de productos
- El precio ya viene incluido en la respuesta del backend

---

**¡El precio ya está disponible en el backend! Solo necesitas mostrarlo en el frontend! 💰**

