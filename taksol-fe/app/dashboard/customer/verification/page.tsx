"use client";

import { useState } from "react";
import api from "@/lib/api";

export default function Verification(){

  const [type,setType] = useState("");
  const [path,setPath] = useState("");

  const submit = async()=>{

    await api.post("/me/verification",{
      documentType:type,
      documentPath:path
    });

    alert("Verification submitted");

  };

  return(

    <div className="space-y-4">

      <h1 className="text-2xl font-bold">
        Identity Verification
      </h1>

      <input
        placeholder="Document Type"
        onChange={e=>setType(e.target.value)}
        className="border p-2 w-full"
      />

      <input
        placeholder="Document Path"
        onChange={e=>setPath(e.target.value)}
        className="border p-2 w-full"
      />

      <button
        onClick={submit}
        className="bg-blue-500 text-white px-4 py-2 rounded"
      >
        Submit
      </button>

    </div>

  );

}