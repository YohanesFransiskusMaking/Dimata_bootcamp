"use client";

import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import Sidebar from "@/components/layout/Sidebar";
import Script from "next/script";

const roleRouteMap: Record<string, string> = {
  SUPER_ADMIN: "/dashboard/super-admin",
  OPS_ADMIN: "/dashboard/ops-admin",
  FINANCE_ADMIN: "/dashboard/finance-admin",
  DRIVER: "/dashboard/driver",
  CUSTOMER: "/dashboard/customer",
};

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const pathname = usePathname();

  const { isAuthenticated, role, logout, isLoading } = useAuth();

  const [authorized, setAuthorized] = useState(false);

  useEffect(() => {
    if (isLoading) return;

    if (!isAuthenticated) {
      router.replace("/login");
      return;
    }

    const allowedPath = roleRouteMap[role || ""];

    if (!allowedPath) {
      logout();
      return;
    }

    if (pathname === "/dashboard") {
      router.replace(allowedPath);
      return;
    }

    if (!pathname.startsWith(allowedPath)) {
      router.replace(allowedPath);
      return;
    }

    setAuthorized(true);
  }, [isAuthenticated, role, pathname]);

  if (isLoading) {
    return <div className="p-10">Loading...</div>;
  }

  if (!authorized) {
    return null;
  }

  return (
    <div className="flex min-h-screen">
      <Sidebar />

      <main className="flex-1 p-10 bg-bgSoft text-textMain">
        {children}

        <Script
          src="https://app.sandbox.midtrans.com/snap/snap.js"
          data-client-key={process.env.NEXT_PUBLIC_MIDTRANS_CLIENT_KEY}
          strategy="afterInteractive"
        />
      </main>
    </div>
  );
}
