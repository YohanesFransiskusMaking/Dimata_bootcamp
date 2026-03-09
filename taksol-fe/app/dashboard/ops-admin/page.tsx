"use client";

import Card from "@/components/ui/Card";

export default function OpsAdminDashboard(){

  return(

    <div className="grid grid-cols-3 gap-6">

      <Card title="Drivers">
        Manage driver accounts
      </Card>

      <Card title="Verification">
        Approve or reject driver applications
      </Card>

      <Card title="Vehicles">
        Monitor registered vehicles
      </Card>

    </div>

  );

}