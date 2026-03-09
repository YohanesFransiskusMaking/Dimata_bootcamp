"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";
import { useAuth } from "@/context/AuthContext";

export default function DriverReviews() {
  const { userId } = useAuth();

  const [reviews, setReviews] = useState<any[]>([]);
  const [orders, setOrders] = useState<any[]>([]);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [selectedOrder, setSelectedOrder] = useState<number | null>(null);

  const fetchReviews = async () => {
    if (!userId) return;

    try {
      const res = await api.get(`/reviews/users/${userId}`);

      setReviews(res.data.content || []);
    } catch {
      setReviews([]);
    }
  };

  const fetchOrders = async () => {
    try {
      const res = await api.get("/orders/driver/history");

      const completedOrders = res.data.filter(
        (o: any) => o.status === "COMPLETED",
      );

      setOrders(completedOrders);
    } catch {
      setOrders([]);
    }
  };

  useEffect(() => {
    fetchReviews();
    fetchOrders();
  }, [userId]);

  const submitReview = async () => {
    if (!selectedOrder) {
      alert("Select order first");
      return;
    }

    try {
      await api.post("/reviews", {
        orderId: selectedOrder,
        rating,
        comment,
      });

      alert("Review submitted");

      setComment("");
      setSelectedOrder(null);

      fetchReviews();
    } catch (err: any) {
      alert(err.response?.data?.message || "Review failed");
    }
  };

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Driver Reviews</h1>

      {/* REVIEWS FROM CUSTOMERS */}

      <div className="space-y-3">
        <h2 className="text-xl font-bold">Reviews From Customers</h2>

        {reviews.length === 0 && <p>No reviews yet</p>}

        {reviews.map((r) => (
          <div key={r.id} className="bg-white p-4 rounded shadow">
            <p>⭐ {r.rating}</p>

            <p>{r.comment}</p>
          </div>
        ))}
      </div>

      {/* REVIEW CUSTOMER */}

      <div className="space-y-3">
        <h2 className="text-xl font-bold">Review Customer</h2>

        <div className="bg-white p-4 rounded shadow space-y-3">
          <select
            className="border p-2 w-full"
            value={selectedOrder ?? ""}
            onChange={(e) => setSelectedOrder(Number(e.target.value))}
          >
            <option value="">Select completed order</option>

            {orders.map((o) => (
              <option key={o.id} value={o.id}>
                {o.lokasiJemput} → {o.lokasiTujuan}
              </option>
            ))}
          </select>

          <input
            type="number"
            min={1}
            max={5}
            value={rating}
            onChange={(e) => setRating(Number(e.target.value))}
            className="border p-2 w-full"
          />

          <textarea
            placeholder="Comment"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            className="border p-2 w-full"
          />

          <button
            onClick={submitReview}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Submit Review
          </button>
        </div>
      </div>
    </div>
  );
}
