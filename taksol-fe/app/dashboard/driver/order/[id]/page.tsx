"use client";

import { useEffect,useState } from "react";
import { useParams } from "next/navigation";
import api from "@/lib/api";

export default function OrderDetail(){

  const {id} = useParams();

  const [order,setOrder] = useState<any>(null);
  const [detail,setDetail] = useState<any>(null);
  const [payment,setPayment] = useState<any>(null);

  const fetchData = async()=>{

    const o = await api.get(`/orders/driver/history`);
    const orderData = o.data.find((x:any)=>x.id == id);

    setOrder(orderData);

    const d = await api.get(`/orders/${id}/detail`);
    setDetail(d.data);

    try{

      const p = await api.get(`/payments/order/${id}`);
      setPayment(p.data);

    }catch{}

  };

  useEffect(()=>{
    fetchData();
  },[]);

  if(!order) return <p>Loading...</p>;

  return(

    <div className="space-y-4">

      <h1 className="text-2xl font-bold">
        Order Detail
      </h1>

      <div className="bg-white p-4 rounded shadow">

        <p>
          {order.lokasiJemput} → {order.lokasiTujuan}
        </p>

        <p>Status : {order.status}</p>

        <p>
          Price : Rp {Number(order.hargaTotal).toLocaleString()}
        </p>

        {detail && (
          <>
            <p>Distance : {detail.jarakKm} km</p>
            <p>Tarif/km : {detail.tarifPerKm}</p>
          </>
        )}

      </div>

      {payment && (

        <div className="bg-white p-4 rounded shadow">

          <h2 className="font-bold">
            Payment
          </h2>

          <p>Method : {payment.metode}</p>

          <p>Status : {payment.status}</p>

          <p>
            Amount : Rp {Number(payment.jumlah).toLocaleString()}
          </p>

        </div>

      )}

    </div>

  );

}