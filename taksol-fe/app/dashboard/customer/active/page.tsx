"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";

interface Order {
  id: number;
  lokasiJemput: string;
  lokasiTujuan: string;
  status: string;
  hargaTotal: number;
}

export default function ActiveOrderPage() {
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);

  const BASE_URL = "http://localhost:8080";
  const router = useRouter();

  const getAuthHeader = () => ({
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
      "X-Active-Role": localStorage.getItem("activeRole"),
    },
  });

  const fetchActiveOrder = async () => {
    try {
      const res = await axios.get(
        `${BASE_URL}/orders/customer/my-active`,
        getAuthHeader()
      );
      setOrder(res.data);
    } catch {
      setOrder(null);
    } finally {
      setLoading(false);
    }
  };

  

  // ================= CANCEL ORDER =================
  const handleCancel = async () => {
    if (!order) return;

    if (!confirm("Yakin ingin membatalkan order ini?")) return;

    try {
      await axios.put(
        `${BASE_URL}/orders/${order.id}/status`,
        { status: "CANCELLED", 
        },

        getAuthHeader()
      );

      alert("Order berhasil dibatalkan");
      fetchActiveOrder(); // refresh data
    } catch (err: any) {
      alert(err.response?.data?.message || "Gagal membatalkan order");
    }
  };

  useEffect(() => {
    fetchActiveOrder();

    const interval = setInterval(fetchActiveOrder, 5000);
    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return <p>Loading...</p>;
  }

  if (!order) {
    return <p>Tidak ada order aktif</p>;
  }

  

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Order Aktif</h1>

      <div className="bg-white p-4 rounded shadow space-y-2">
        <p>
          {order.lokasiJemput} → {order.lokasiTujuan}
        </p>
        <p>Status: {order.status}</p>
        <p>Total: Rp {order.hargaTotal}</p>

        {order.status === "PENDING" && (
          <button
            onClick={handleCancel}
            className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-800"
          >
            Cancel Order
          </button>
        )}
      </div>
    </div>
  );
}