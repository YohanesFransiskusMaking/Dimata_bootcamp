"use client";

import { useEffect,useState } from "react";
import api from "@/lib/api";

export default function DriverDashboard(){

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
        Driver Dashboard
      </h1>

      {!vehicle && (

        <div className="bg-yellow-100 p-4 rounded">

          <p>
            Please register your vehicle first
          </p>

        </div>

      )}

    </div>

  );

}