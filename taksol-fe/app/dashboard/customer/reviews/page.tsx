"use client";

import { useState, useEffect } from "react";
import api from "@/lib/api";

export default function ReviewsPage() {

  const [tab,setTab] = useState("write");

  const [orderId,setOrderId] = useState("");
  const [rating,setRating] = useState(5);
  const [comment,setComment] = useState("");

  const [myReviews,setMyReviews] = useState([]);
  const [driverId,setDriverId] = useState("");
  const [driverReviews,setDriverReviews] = useState([]);

  const [userId,setUserId] = useState<number|null>(null);

  // ambil user id
  const fetchUser = async ()=>{

    const res = await api.get("/user/me");

    setUserId(res.data.id);

  };

  // review tentang customer
  const fetchMyReviews = async ()=>{

    if(!userId) return;

    const res = await api.get(`/reviews/users/${userId}`);

    setMyReviews(res.data.content);

  };

  // review driver
  const fetchDriverReviews = async ()=>{

    if(!driverId) return;

    const res = await api.get(`/reviews/users/${driverId}`);

    setDriverReviews(res.data.content);

  };

  useEffect(()=>{
    fetchUser();
  },[]);

  useEffect(()=>{
    fetchMyReviews();
  },[userId]);

  // submit review
  const submitReview = async () => {

    try {

      await api.post("/reviews",{
        orderId:Number(orderId),
        rating,
        comment
      });

      alert("Review berhasil dikirim");

    } catch (err:any) {

      alert(err.response?.data?.message || "Gagal submit review");

    }

  };

  return (

    <div className="space-y-6">

      <h1 className="text-2xl font-bold">
        Reviews
      </h1>

      {/* TAB MENU */}

      <div className="flex gap-4">

        <button
          onClick={()=>setTab("write")}
          className="bg-primary text-white px-3 py-1 rounded"
        >
          Write Review
        </button>

        <button
          onClick={()=>setTab("my")}
          className="bg-primary text-white px-3 py-1 rounded"
        >
          Reviews About Me
        </button>

        <button
          onClick={()=>setTab("driver")}
          className="bg-primary text-white px-3 py-1 rounded"
        >
          Driver Reviews
        </button>

      </div>

      {/* WRITE REVIEW */}

      {tab==="write" && (

        <div className="bg-card p-6 rounded shadow space-y-3">

          <input
            placeholder="Order ID"
            className="border p-2 w-full"
            onChange={(e)=>setOrderId(e.target.value)}
          />

          <input
            type="number"
            className="border p-2 w-full"
            value={rating}
            onChange={(e)=>setRating(Number(e.target.value))}
          />

          <textarea
            placeholder="Comment"
            className="border p-2 w-full"
            onChange={(e)=>setComment(e.target.value)}
          />

          <button
            onClick={submitReview}
            className="bg-primary text-white px-4 py-2 rounded"
          >
            Submit
          </button>

        </div>

      )}

      {/* REVIEWS ABOUT ME */}

      {tab==="my" && (

        <div className="space-y-4">

          {myReviews.map((r:any)=>(
            <div
              key={r.id}
              className="bg-card p-4 rounded shadow"
            >

              <p>⭐ {r.rating}</p>

              <p>{r.comment}</p>

            </div>
          ))}

        </div>

      )}

      {/* DRIVER REVIEWS */}

      {tab==="driver" && (

        <div className="space-y-4">

          <input
            placeholder="Driver ID"
            className="border p-2 w-full"
            onChange={(e)=>setDriverId(e.target.value)}
          />

          <button
            onClick={fetchDriverReviews}
            className="bg-primary text-white px-4 py-2 rounded"
          >
            Load Reviews
          </button>

          {driverReviews.map((r:any)=>(
            <div
              key={r.id}
              className="bg-card p-4 rounded shadow"
            >

              <p>⭐ {r.rating}</p>

              <p>{r.comment}</p>

            </div>
          ))}

        </div>

      )}

    </div>

  );

}