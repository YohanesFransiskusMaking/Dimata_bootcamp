"use client";

import { useEffect,useState } from "react";
import api from "@/lib/api";

export default function VerificationPage(){

  const [list,setList] = useState<any[]>([]);

  const fetch = async()=>{

    const res = await api.get("/admin/verification");

    setList(res.data);

  };

  useEffect(()=>{
    fetch();
  },[]);

  const approve = async(id:number)=>{

    await api.put(`/admin/verification/${id}/approve`);

    fetch();

  };

  const reject = async(id:number)=>{

    const reason = prompt("Reject reason");

    await api.put(`/admin/verification/${id}/reject`,{
      reason
    });

    fetch();

  };

  return(

    <div className="space-y-4">

      <h1 className="text-2xl font-bold">
        KYC Verification
      </h1>

      {list.map(v=>(

        <div key={v.userId}
        className="bg-white p-4 rounded shadow">

          <p>User : {v.userId}</p>

          <p>Status : {v.status}</p>

          <button
          onClick={()=>approve(v.userId)}
          className="bg-green-500 text-white px-3 py-1 mr-2">
            Approve
          </button>

          <button
          onClick={()=>reject(v.userId)}
          className="bg-red-500 text-white px-3 py-1">
            Reject
          </button>

        </div>

      ))}

    </div>

  );

}