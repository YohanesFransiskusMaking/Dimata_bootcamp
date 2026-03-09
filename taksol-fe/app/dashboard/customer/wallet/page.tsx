"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";

declare global {
  interface Window {
    snap: any;
  }
}

export default function WalletPage() {
  const [balance, setBalance] = useState(0);
  const [amount, setAmount] = useState("");

  const fetchWallet = async () => {
    const res = await api.get("/wallet");
    setBalance(res.data.balance);
  };

  const handleTopup = async () => {
    const res = await api.post("/wallet/topup/midtrans", {
      amount: Number(amount),
    });

    if (window.snap) {
      window.snap.pay(res.data.snapToken, {
        onSuccess: () => fetchWallet(),
        onPending: () => alert("Menunggu pembayaran"),
        onError: () => alert("Pembayaran gagal"),
      });
    }
  };

  useEffect(() => {
    fetchWallet();
  }, []);

  return (
    <div className="max-w-lg space-y-6">
      <h1 className="text-2xl font-bold">Wallet</h1>

      <div className="bg-white p-6 rounded shadow space-y-4">
        <div className="text-3xl font-bold text-primary">
          Rp {balance.toLocaleString()}
        </div>

        <input
          className="border p-2 w-full"
          placeholder="Topup amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />

        <button
          onClick={handleTopup}
          className="bg-primary text-white px-4 py-2 rounded w-full"
        >
          Top Up Wallet
        </button>
      </div>
    </div>
  );
}
