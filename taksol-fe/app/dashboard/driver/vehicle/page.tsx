"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";

export default function VehiclePage(){

  const [vehicle,setVehicle] = useState<any>(null);

  const fetchVehicle = async()=>{

    try{

      const res = await api.get("/kendaraan/me");
      setVehicle(res.data);

    }catch{

      setVehicle(null);

    }

  };

  useEffect(()=>{
    fetchVehicle();
  },[]);

  return(

    <div className="space-y-6">

      <h1 className="text-2xl font-bold">
        My Vehicle
      </h1>

      {!vehicle ? (
        <p>No vehicle registered</p>
      ) : (

        <div className="bg-white p-4 rounded shadow">

          <p>Plate : {vehicle.platNomor}</p>
          <p>Status : {vehicle.status}</p>

        </div>

      )}

    </div>
  );
}