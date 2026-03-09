"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";

export default function ConfigPage(){

  const [configs,setConfigs] = useState([]);

  const fetchConfigs = async()=>{
    const res = await api.get("/config");
    setConfigs(res.data);
  };

  useEffect(()=>{
    fetchConfigs();
  },[]);

  return(

    <div className="space-y-4">

      <h1 className="text-2xl font-bold">
        App Config
      </h1>

      {configs.map((c:any)=>(
        <div key={c.configKey}
        className="bg-white p-3 rounded shadow">

          <p>{c.configKey}</p>
          <p>{c.configValue}</p>

        </div>
      ))}

    </div>
  );
}