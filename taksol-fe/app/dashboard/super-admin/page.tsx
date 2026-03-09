"use client";

import StatsCard from "@/components/ui/StatsCard";
import EarningsChart from "@/components/charts/EarningsChart";
import { FaUsers, FaCar, FaMoneyBill } from "react-icons/fa";

export default function SuperAdminDashboard(){

  return (

    <div className="space-y-6">

      <div className="grid grid-cols-3 gap-6">

        <StatsCard
          title="Total Users"
          value="120"
          icon={<FaUsers />}
        />

        <StatsCard
          title="Active Drivers"
          value="35"
          icon={<FaCar />}
        />

        <StatsCard
          title="Revenue"
          value="Rp 25.000.000"
          icon={<FaMoneyBill />}
        />

      </div>

      <EarningsChart />

    </div>

  );
}