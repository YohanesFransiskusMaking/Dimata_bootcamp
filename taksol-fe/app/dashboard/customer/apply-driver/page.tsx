"use client";

import { useState } from "react";
import api from "@/lib/api";

export default function ApplyDriver(){

  const [license,setLicense] = useState("");
  const [experience,setExperience] = useState("");

  const submit = async()=>{

    await api.post("/customers/apply",{
      licenseNumber:license,
      experienceYears:Number(experience)
    });

    alert("Application submitted");

  };

  return(

    <div className="space-y-4">

      <h1 className="text-2xl font-bold">
        Apply Driver
      </h1>

      <input
        placeholder="License Number"
        value={license}
        onChange={e=>setLicense(e.target.value)}
        className="border p-2 w-full"
      />

      <input
        placeholder="Experience Years"
        value={experience}
        onChange={e=>setExperience(e.target.value)}
        className="border p-2 w-full"
      />

      <button
        onClick={submit}
        className="bg-blue-500 text-white px-4 py-2 rounded"
      >
        Submit Application
      </button>

    </div>

  );

}