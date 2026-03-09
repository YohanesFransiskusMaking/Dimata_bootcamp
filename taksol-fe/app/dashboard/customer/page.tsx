"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";
import Card from "@/components/ui/Card";

export default function CustomerDashboard() {

  const [balance,setBalance] = useState(0);

  const fetchWallet = async () => {

    try {

      const res = await api.get("/wallet");

      setBalance(res.data.balance);

    } catch (err) {

      console.error("Failed fetch wallet");

    }

  };

  useEffect(()=>{
    fetchWallet();
  },[]);

  return (

    <div className="grid grid-cols-2 gap-6">

      <Card title="Active Order">
        Cek menu Active Order
      </Card>

      <Card title="Wallet Balance">
        Rp {balance.toLocaleString()}
      </Card>

    </div>

  );

}