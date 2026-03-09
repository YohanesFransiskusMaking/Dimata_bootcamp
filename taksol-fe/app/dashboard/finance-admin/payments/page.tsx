"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";
import DataTable from "@/components/table/DataTable";

export default function PaymentsPage(){

  const [payments,setPayments] = useState([]);

  const fetchPayments = async()=>{
    const res = await api.get("/payments");
    setPayments(res.data);
  };

  useEffect(()=>{
    fetchPayments();
  },[]);

  return(

    <div className="space-y-6">

      <h1 className="text-2xl font-bold">
        Payments
      </h1>

      <DataTable
        columns={[
          "id",
          "jumlah",
          "paymentStatus",
          "metode"
        ]}
        data={payments}
      />

    </div>
  );
}