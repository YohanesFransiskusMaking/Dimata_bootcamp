"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";

export default function DriverActiveOrder() {
  const [order, setOrder] = useState<any>(null);
  const [payment, setPayment] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  const fetchOrder = async () => {
    setLoading(true);

    try {
      const res = await api.get("/orders/driver/my-active");

      if (res.data) {
        setOrder(res.data);
        await fetchPayment(res.data.id);
        setLoading(false);
        return;
      }
    } catch {}

    // fallback cek history
    try {
      const history = await api.get("/orders/driver/history");

      const historyData = Array.isArray(history.data) ? history.data : [];

      const unpaidOrders = historyData
        .filter(
          (o: any) =>
            o.status === "COMPLETED" && o.statusPembayaran === "UNPAID",
        )
        .sort(
          (a: any, b: any) =>
            new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
        );

      const latestUnpaid = unpaidOrders[0];

      if (latestUnpaid) {
        setOrder(latestUnpaid);
        await fetchPayment(latestUnpaid.id);
      } else {
        setOrder(null);
        setPayment(null);
      }
    } catch {
      setOrder(null);
      setPayment(null);
    }

    setLoading(false);
  };

  const fetchPayment = async (orderId: number) => {
    try {
      const res = await api.get(`/payments/order/${orderId}`);
      setPayment(res.data);
    } catch {
      setPayment(null);
    }
  };

  useEffect(() => {
    fetchOrder();
  }, []);

  const startTrip = async () => {
    await api.put(`/orders/${order.id}/status`, {
      status: "ON_PROGRESS",
    });

    fetchOrder();
  };

  const completeTrip = async () => {
    await api.put(`/orders/${order.id}/status`, {
      status: "COMPLETED",
    });

    setOrder({
      ...order,
      status: "COMPLETED",
      statusPembayaran: "UNPAID",
    });

    await fetchPayment(order.id);
  };

  const confirmCashPayment = async () => {
    try {
      if (!payment?.id) {
        alert("Payment tidak ditemukan");
        return;
      }

      await api.put(`/payments/${payment.id}/confirm-cash`);

      alert("Cash payment confirmed");

      fetchOrder();
    } catch (err: any) {
      console.log("CONFIRM CASH ERROR:", err);

      alert("Cash confirmation gagal");
    }
  };

  if (loading) {
    return <p>Loading...</p>;
  }

  if (!order) {
    return (
      <div>
        <h1 className="text-2xl font-bold">Active Order</h1>
        <p>No active order</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Active Order</h1>

      <div className="bg-white p-4 rounded shadow space-y-2">
        <p>
          {order.lokasiJemput} → {order.lokasiTujuan}
        </p>

        <p>Status : {order.status}</p>

        <p>Payment Status : {order.statusPembayaran}</p>

        {payment && <p>Payment Method : {payment.metode}</p>}

        {order.status === "ACCEPTED" && (
          <button
            onClick={startTrip}
            className="bg-blue-500 text-white px-3 py-1 rounded"
          >
            Start Trip
          </button>
        )}

        {order.status === "ON_PROGRESS" && (
          <button
            onClick={completeTrip}
            className="bg-green-500 text-white px-3 py-1 rounded"
          >
            Complete Trip
          </button>
        )}

        {order.status === "COMPLETED" &&
          order.statusPembayaran === "UNPAID" &&
          payment?.metode === "CASH" && (
            <button
              onClick={confirmCashPayment}
              className="bg-yellow-500 text-white px-3 py-1 rounded"
            >
              Confirm Cash Payment
            </button>
          )}
      </div>
    </div>
  );
}
