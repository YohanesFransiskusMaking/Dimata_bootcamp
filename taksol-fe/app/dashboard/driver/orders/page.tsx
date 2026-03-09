"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";
import { connectSocket } from "@/lib/socket";

interface Order {
  id: number;
  lokasiJemput: string;
  lokasiTujuan: string;
}

export default function DriverOrdersPage(){

  const [orders,setOrders] = useState<Order[]>([]);

  const fetchOrders = async()=>{
    const res = await api.get("/orders/available");
    setOrders(res.data);
  };

  useEffect(()=>{

    fetchOrders();

    connectSocket((data)=>{

      if(data.type === "NEW_ORDER"){

        const newOrder = {
          id:data.orderId,
          lokasiJemput:data.pickup,
          lokasiTujuan:data.destination
        };

        setOrders(prev => [...prev,newOrder]);

      }

    });

  },[]);

  const acceptOrder = async(id:number)=>{
    await api.post(`/orders/${id}/accept`);
    fetchOrders();
  };

  return(

    <div className="space-y-4">

      <h1 className="text-2xl font-bold">
        Available Orders
      </h1>

      {orders.map((o)=>(
        <div key={o.id}
        className="bg-white p-4 rounded shadow">

          <p>{o.lokasiJemput} → {o.lokasiTujuan}</p>

          <button
            onClick={()=>acceptOrder(o.id)}
            className="bg-blue-500 text-white px-3 py-1 rounded"
          >
            Accept
          </button>

        </div>
      ))}

    </div>
  );
}