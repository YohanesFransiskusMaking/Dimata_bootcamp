"use client";

import { useEffect, useState } from "react";
import api from "@/lib/api";
import DataTable from "@/components/table/DataTable";

export default function UsersPage() {

  const [users,setUsers] = useState([]);

  useEffect(()=>{
    fetchUsers();
  },[]);

  const fetchUsers = async()=>{
    const res = await api.get("/users");
    setUsers(res.data);
  };

  return (
    <div className="space-y-6">

      <h1 className="text-2xl font-bold">
        Users
      </h1>

      <DataTable
        columns={["id","nama","email","noHp"]}
        data={users}
      />

    </div>
  );
}