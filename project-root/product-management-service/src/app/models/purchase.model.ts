export interface PurchaseRequest {
  productId: number;
  quantity: number;
}

export interface PurchaseResponse {
  message: string;
  remainingQuantity: number;
}
