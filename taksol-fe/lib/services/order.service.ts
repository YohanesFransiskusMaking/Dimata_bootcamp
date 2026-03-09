import api from "../api";
import { OrderStatus } from "@/types/order";

export const orderService = {
  async createOrder(data: {
    lokasiJemput: string;
    lokasiTujuan: string;
    originLat: number;
    originLng: number;
    destinationLat: number;
    destinationLng: number;
  }) {
    const response = await api.post("/orders", data);
    return response.data;
  },

  async chooseVehicle(orderId: number, jenisId: number) {
    const response = await api.post(`/orders/${orderId}/vehicle/${jenisId}`);
    return response.data;
  },

  async acceptOrder(orderId: number) {
    const response = await api.post(`/orders/${orderId}/accept`);
    return response.data;
  },

  async updateStatus(orderId: number, status: OrderStatus) {
    const response = await api.put(`/orders/${orderId}/status`, { status });
    return response.data;
  },
};
