"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";
import Link from "next/link";

export default function DriverHistory(){

  const [orders,setOrders] = useState<any[]>([]);

  const fetchOrders = async()=>{

    const res = await api.get("/orders/driver/history");
    setOrders(res.data);

  };

  useEffect(()=>{
    fetchOrders();
  },[]);

  return(

    <div className="space-y-4">

      <h1 className="text-2xl font-bold">
        Trip History
      </h1>

      {orders.map((o)=>(
        <div
        key={o.id}
        className="bg-white p-4 rounded shadow">

          <p>
            {o.lokasiJemput} → {o.lokasiTujuan}
          </p>

          <p>Status : {o.status}</p>

          <p>
            Rp {Number(o.hargaTotal).toLocaleString()}
          </p>

          <Link
          href={`/dashboard/driver/order/${o.id}`}
          className="text-blue-600">
            View Detail
          </Link>

        </div>
      ))}

    </div>

  );

}