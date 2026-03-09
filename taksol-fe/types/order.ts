export enum OrderStatus {
  PENDING = "PENDING",
  ACCEPTED = "ACCEPTED",
  ON_PROGRESS = "ON_PROGRESS",
  COMPLETED = "COMPLETED",
  CANCELLED = "CANCELLED",
}

export interface Order {
  id: string;
  customerId: string;
  driverId?: string | null;
  pickupLocation: string;
  destinationLocation: string;
  price: number;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderRequest {
  lokasiJemput: string;
  lokasiTujuan: string;
  originLat: number;
  originLng: number;
  destinationLat: number;
  destinationLng: number;
}