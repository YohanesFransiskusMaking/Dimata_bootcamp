"use client";

import { useAuth } from "@/context/AuthContext";

export default function Navbar() {

  const { email, logout } = useAuth();

  return (
    <header className="bg-white shadow p-4 flex justify-between">

      <h2 className="font-semibold">
        TAKSOL Dashboard
      </h2>

      <div className="flex items-center gap-4">

        <span className="text-sm text-gray-600">
          {email}
        </span>

        <button
          onClick={logout}
          className="bg-red-500 text-white px-3 py-1 rounded"
        >
          Logout
        </button>

      </div>
    </header>
  );
}