/**
 * Product model matching backend ProductResponse DTO
 */
export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  price: number;
  status: boolean;
  createdAt?: string;
  updatedAt?: string;
  stock?: number; // Stock from Inventory Service
}

/**
 * Product Request model matching backend ProductRequest DTO
 * Used for creating new products
 */
export interface ProductRequest {
  sku: string;
  name: string;
  description: string;
  price: number;
  status: boolean;
}

/**
 * Inventory model matching backend InventoryResponse DTO
 */
export interface Inventory {
  id: number;
  sku: string;
  quantity: number; // Backend field name
  createdAt?: string;
  updatedAt?: string;
}

/**
 * Page response from backend (Spring Data Page)
 */
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
