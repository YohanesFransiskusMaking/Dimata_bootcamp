"use client";

import Sidebar from "./Sidebar";
import Navbar from "./Navbar";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex min-h-screen bg-bgSoft">

      <Sidebar />

      <div className="flex flex-col flex-1">

        <Navbar />

        <main className="p-6">{children}</main>

      </div>
    </div>
  );
}