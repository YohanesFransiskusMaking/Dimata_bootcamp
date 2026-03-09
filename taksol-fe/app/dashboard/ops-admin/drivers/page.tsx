"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";

export default function DriversPage() {
  const [drivers, setDrivers] = useState<any[]>([]);

  const fetch = async () => {
    const res = await api.get("/admin/drivers/pending");

    setDrivers(res.data);
  };

  useEffect(() => {
    fetch();
  }, []);

  const approve = async (id: number) => {
    await api.put(`/admin/drivers/${id}/approve`);

    fetch();
  };

  const reject = async (id: number) => {
    const reason = prompt("Reject reason");

    await api.put(`/admin/drivers/${id}/reject`, {
      reason,
    });

    fetch();
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Driver Applications</h1>

      {drivers.map((d) => (
        <div key={d.userId} className="bg-white p-4 rounded shadow">
          <p>Name : {d.nama}</p>

          <p>Status : {d.status}</p>

          <button
            onClick={() => approve(d.userId)}
            className="bg-green-500 text-white px-3 py-1 mr-2"
          >
            Approve
          </button>

          <button
            onClick={() => reject(d.userId)}
            className="bg-red-500 text-white px-3 py-1"
          >
            Reject
          </button>
        </div>
      ))}
    </div>
  );
}
