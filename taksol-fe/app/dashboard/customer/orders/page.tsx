"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";

export default function OrdersPage(){

  const [orders,setOrders] = useState([]);

  const fetchOrders = async ()=>{
    const res = await api.get("/orders/customer");
    setOrders(res.data);
  };

  useEffect(()=>{
    fetchOrders();
  },[]);

  return(

    <div className="space-y-6">

      <h1 className="text-2xl font-bold">
        Order History
      </h1>

      <table className="w-full bg-white rounded shadow">

        <thead>
          <tr className="bg-primarySoft">
            <th>ID</th>
            <th>Route</th>
            <th>Status</th>
            <th>Price</th>
            <th>Payment</th>
          </tr>
        </thead>

        <tbody>

          {orders.map((o:any)=>(
            <tr key={o.id} className="border-t">

              <td>{o.id}</td>

              <td>
                {o.lokasiJemput} → {o.lokasiTujuan}
              </td>

              <td>{o.status}</td>

              <td>
                Rp {o.hargaTotal}
              </td>

              <td>
                {o.statusPembayaran}
              </td>

            </tr>
          ))}

        </tbody>

      </table>

    </div>

  );

}