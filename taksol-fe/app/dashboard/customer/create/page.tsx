"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";
import MapPicker from "../MapPicker";

/* ================= TYPES ================= */

type Location = {
  lat: number | null;
  lng: number | null;
  address: string;
};

type Vehicle = {
  id: number;
  namaJenis: string;
  tarifPerKm: number;
};

type Estimate = {
  distanceKm: number;
  estimatedPrice: number;
};

type MapSelectType = "origin" | "destination";

/* ================= PAGE ================= */

export default function CreateOrderPage() {
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [vehicleId, setVehicleId] = useState("");

  const [origin, setOrigin] = useState<Location>({
    lat: null,
    lng: null,
    address: "",
  });

  const [destination, setDestination] = useState<Location>({
    lat: null,
    lng: null,
    address: "",
  });

  const [estimate, setEstimate] = useState<Estimate | null>(null);

  /* ================= FETCH VEHICLES ================= */

  const fetchVehicles = async () => {
    const res = await api.get("/jenis_kendaraan");
    setVehicles(res.data);
  };

  useEffect(() => {
    fetchVehicles();
  }, []);

  /* ================= MAP SELECT ================= */

  const handleMapSelect = (
    type: MapSelectType,
    lat: number,
    lng: number,
    address: string,
  ) => {
    if (type === "origin") {
      setOrigin({
        lat,
        lng,
        address,
      });
    } else {
      setDestination({
        lat,
        lng,
        address,
      });
    }
  };

  /* ================= ESTIMATE PRICE ================= */

  const estimatePrice = async () => {
    if (
      origin.lat === null ||
      origin.lng === null ||
      destination.lat === null ||
      destination.lng === null
    ) {
      alert("Pilih lokasi di map terlebih dahulu");
      return;
    }

    if (!vehicleId) {
      alert("Pilih jenis kendaraan");
      return;
    }

    try {
      const res = await api.post("/orders/estimate", {
        originLat: origin.lat,
        originLng: origin.lng,
        destinationLat: destination.lat,
        destinationLng: destination.lng,
        jenisKendaraanId: Number(vehicleId),
      });

      console.log("ESTIMATE:", res.data);

      setEstimate(res.data);
    } catch (err: any) {
      console.log("ESTIMATE ERROR:", err.response?.data);

      alert(err.response?.data?.message || "Gagal menghitung estimasi");
    }
  };

  /* ================= CREATE ORDER ================= */

  const createOrder = async () => {
    if (!estimate) {
      alert("Hitung estimasi terlebih dahulu");
      return;
    }

    try {
      const res = await api.post("/orders", {
        lokasiJemput: origin.address || "Pickup",
        lokasiTujuan: destination.address || "Destination",

        originLat: origin.lat,
        originLng: origin.lng,

        destinationLat: destination.lat,
        destinationLng: destination.lng,

        jenisKendaraanId: Number(vehicleId),
      });

      console.log("ORDER CREATED:", res.data);

      alert("Order berhasil dibuat");

      setEstimate(null);
    } catch (err: any) {
      console.log("ORDER ERROR:", err.response?.data);

      alert(err.response?.data?.message || "Create order gagal");
    }
  };

  /* ================= UI ================= */

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-textMain">Create Order</h1>

      {/* MAP PICKER */}

      <MapPicker onSelect={handleMapSelect} />

      {/* VEHICLE SELECT */}

      <div className="bg-card p-6 rounded-xl shadow">
        <label className="block mb-2 font-semibold">Vehicle Type</label>

        <select
          className="border p-3 rounded w-full"
          value={vehicleId}
          onChange={(e) => setVehicleId(e.target.value)}
        >
          <option value="">Select Vehicle</option>

          {vehicles.map((v) => (
            <option key={v.id} value={v.id}>
              {v.namaJenis} - Rp {v.tarifPerKm}/km
            </option>
          ))}
        </select>
      </div>

      {/* ESTIMATE BUTTON */}

      <button
        onClick={estimatePrice}
        className="bg-primary text-white px-4 py-2 rounded"
      >
        Estimate Price
      </button>

      {/* ESTIMATE RESULT */}

      {estimate && (
        <div className="bg-primarySoft p-6 rounded space-y-2">
          <p>Distance : {Number(estimate.distanceKm).toFixed(2)} km</p>

          <p className="text-xl font-bold">
            Estimated Price : Rp{" "}
            {Number(estimate.estimatedPrice).toLocaleString()}
          </p>

          <button
            onClick={createOrder}
            className="bg-primaryDark text-white px-4 py-2 rounded"
          >
            Order Now
          </button>
        </div>
      )}
    </div>
  );
}
