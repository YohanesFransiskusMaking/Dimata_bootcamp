"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/lib/api";
import { jwtDecode } from "jwt-decode";
import { v4 as uuidv4 } from "uuid";

interface JwtPayload {
  groups: string[];
  sub: string;
}

export default function LoginPage() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async (e: React.SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      // Ambil deviceId dari localStorage, generate jika belum ada
      let deviceId = localStorage.getItem("deviceId");
      if (!deviceId) {
        deviceId = uuidv4();
        localStorage.setItem("deviceId", deviceId);
      }

      const response = await api.post("/auth/login", {
        email,
        password,
        deviceId,
      });

      const accessToken = response.data.accessToken;
      const refreshToken = response.data.refreshToken;

      localStorage.setItem("token", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
      window.location.href = "/dashboard";

    } catch (err: any) {
      setError("Login gagal. Periksa email/password.");
    }
  };

  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">
      <form
        onSubmit={handleLogin}
        className="bg-white p-8 rounded-xl shadow-md w-96"
      >
        <h1 className="text-2xl font-bold mb-6 text-center">TAKSOL Login</h1>

        {error && (
          <p className="text-red-500 text-sm mb-4 text-center">{error}</p>
        )}

        <input
          type="email"
          placeholder="Email"
          className="w-full border p-2 mb-4 rounded"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <input
          type="password"
          placeholder="Password"
          className="w-full border p-2 mb-4 rounded"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button
          type="submit"
          className="w-full bg-black text-white p-2 rounded hover:bg-gray-800"
        >
          Login
        </button>
      </form>
    </div>
  );
}
