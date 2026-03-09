"use client";

import { useEffect,useState } from "react";
import api from "@/lib/api";

export default function ApplyStatus(){

  const [status,setStatus] = useState("");

  const fetchStatus = async()=>{

    const res = await api.get("/customers/apply/status");

    setStatus(res.data.status);

  };

  useEffect(()=>{
    fetchStatus();
  },[]);

  return(

    <div>

      <h1 className="text-2xl font-bold">
        Driver Application Status
      </h1>

      <p className="mt-4">
        Status : {status}
      </p>

    </div>

  );

}