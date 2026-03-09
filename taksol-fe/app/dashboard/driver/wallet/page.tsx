"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";

export default function DriverWallet() {
  const [wallet, setWallet] = useState<any>(null);
  const [trx, setTrx] = useState<any[]>([]);

  const fetchData = async () => {
    const w = await api.get("/wallet");
    setWallet(w.data);

    try {
      const t = await api.get("/wallet/transactions");

      const data = Array.isArray(t.data) ? t.data : t.data.transactions || [];

      setTrx(data);
    } catch {
      setTrx([]);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  if (!wallet) return <p>Loading...</p>;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Wallet</h1>

      <div className="bg-white p-6 rounded shadow">
        <p>Balance</p>

        <p className="text-3xl font-bold">
          Rp {Number(wallet.balance).toLocaleString()}
        </p>
      </div>

      <div className="space-y-3">
        <h2 className="text-xl font-bold">Transactions</h2>

        {trx.map((t) => (
          <div key={t.id} className="bg-white p-3 rounded shadow">
            <p>{t.type}</p>

            <p>Rp {Number(t.amount).toLocaleString()}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
