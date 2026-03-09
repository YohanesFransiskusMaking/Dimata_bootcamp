"use client";

import { useEffect,useState } from "react";
import api from "@/lib/api";

export default function VehiclesPage(){

  const [vehicles,setVehicles] = useState<any[]>([]);

  const fetchVehicles = async()=>{

    try{

      const res = await api.get("/kendaraan");

      setVehicles(res.data);

    }catch{

      setVehicles([]);

    }

  };

  useEffect(()=>{
    fetchVehicles();
  },[]);

  const updateStatus = async(id:number,status:string)=>{

    await api.put(`/kendaraan/${id}/status`,{
      status
    });

    fetchVehicles();

  };

  return(

    <div className="space-y-6">

      <h1 className="text-2xl font-bold">
        Vehicles
      </h1>

      {vehicles.length === 0 && (
        <p>No vehicles</p>
      )}

      {vehicles.map((v)=>(
        <div
          key={v.id}
          className="bg-white p-4 rounded shadow space-y-2"
        >

          <p><b>{v.platNomor}</b></p>

          <p>Driver ID : {v.driverId}</p>

          <p>Vehicle Type : {v.jenisKendaraanId}</p>

          <p>Status : {v.status}</p>

          <div className="flex gap-2">

            <button
              onClick={()=>updateStatus(v.id,"ACTIVE")}
              className="bg-green-500 text-white px-2 py-1 rounded"
            >
              Activate
            </button>

            <button
              onClick={()=>updateStatus(v.id,"MAINTENANCE")}
              className="bg-yellow-500 text-white px-2 py-1 rounded"
            >
              Maintenance
            </button>

            <button
              onClick={()=>updateStatus(v.id,"INACTIVE")}
              className="bg-red-500 text-white px-2 py-1 rounded"
            >
              Disable
            </button>

          </div>

        </div>
      ))}

    </div>

  );

}