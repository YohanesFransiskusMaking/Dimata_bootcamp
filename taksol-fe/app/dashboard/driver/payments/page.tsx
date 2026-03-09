"use client";

import { useEffect,useState } from "react";
import api from "@/lib/api";

export default function DriverPayments(){

  const [payments,setPayments] = useState<any[]>([]);

  const fetchPayments = async()=>{

    try{

      const res = await api.get("/payments/driver");

      setPayments(res.data);

    }catch{

      setPayments([]);

    }

  };

  useEffect(()=>{
    fetchPayments();
  },[]);

  return(

    <div className="space-y-4">

      <h1 className="text-2xl font-bold">
        Payments
      </h1>

      {payments.length === 0 && (
        <p>No payments yet</p>
      )}

      {payments.map((p)=>(

        <div
        key={p.id}
        className="bg-white p-4 rounded shadow">

          <p>Order #{p.orderId}</p>

          <p>Method : {p.metode}</p>

          <p>Status : {p.status}</p>

          <p>
            Rp {Number(p.jumlah).toLocaleString()}
          </p>

        </div>

      ))}

    </div>

  );

}