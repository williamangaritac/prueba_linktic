/**
 * Order Item model for creating orders
 */
export interface OrderItem {
  sku: string;
  price: number;
  quantity: number;
}

/**
 * Order Request model
 */
export interface OrderRequest {
  customerEmail: string;
  orderItems: OrderItem[];
}

/**
 * Order Item Response model
 */
export interface OrderItemResponse {
  id: number;
  sku: string;
  price: number;
  quantity: number;
}

/**
 * Order Response model from backend
 */
export interface OrderResponse {
  id: number;
  orderNumber: string;
  orderItems: OrderItemResponse[];
}

/**
 * Purchase Response from backend
 */
export interface PurchaseResponse {
  success: boolean;
  message: string;
  order?: OrderResponse;
  orderNumber?: string;
  notification?: string;
  error?: string;
}

