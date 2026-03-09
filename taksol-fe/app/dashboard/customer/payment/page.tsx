"use client";

import { useState } from "react";
import api from "@/lib/api";

export default function PaymentPage(){

  const [orderId,setOrderId] = useState("");
  const [method,setMethod] = useState("WALLET");
  const [amount,setAmount] = useState("");

  const pay = async ()=>{

    await api.post("/payments",{
      orderId:Number(orderId),
      metode:method,
      jumlah:Number(amount)
    });

    alert("Payment success");

  };

  return(

    <div className="bg-card p-6 rounded-xl shadow space-y-4">

      <h1 className="text-xl font-bold">
        Payment
      </h1>

      <input
        placeholder="Order ID"
        className="border p-2 w-full"
        onChange={(e)=>setOrderId(e.target.value)}
      />

      <input
        placeholder="Amount"
        className="border p-2 w-full"
        onChange={(e)=>setAmount(e.target.value)}
      />

      <select
        className="border p-2 w-full"
        onChange={(e)=>setMethod(e.target.value)}
      >
        <option value="WALLET">Wallet</option>
        <option value="CASH">Cash</option>
        <option value="CARD">Card</option>
      </select>

      <button
        onClick={pay}
        className="bg-primary text-white p-2 rounded"
      >
        Pay
      </button>

    </div>

  );

}