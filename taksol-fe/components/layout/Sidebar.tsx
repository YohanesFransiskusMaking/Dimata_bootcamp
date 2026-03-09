"use client";

import Link from "next/link";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export default function Sidebar() {
  const { role, roles, setRole, logout } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  const menus: Record<string, any[]> = {
    CUSTOMER: [
      { name: "Dashboard", path: "/dashboard/customer" },

      { section: "ORDERS" },

      { name: "Create Order", path: "/dashboard/customer/create" },
      { name: "Active Order", path: "/dashboard/customer/active" },
      { name: "Order History", path: "/dashboard/customer/orders" },

      { section: "FINANCE" },

      { name: "Wallet", path: "/dashboard/customer/wallet" },
      { name: "Payments", path: "/dashboard/customer/payment" },

      { section: "DRIVER PROGRAM" },

      { name: "Apply Driver", path: "/dashboard/customer/apply-driver" },
      { name: "Verification", path: "/dashboard/customer/verification" },
      { name: "Application Status", path: "/dashboard/customer/apply-status" },

      { name: "Reviews", path: "/dashboard/customer/reviews" },
    ],

    DRIVER: [
      { name: "Dashboard", path: "/dashboard/driver", icon: "🏠" },

      { section: "ORDERS" },

      {
        name: "Available Orders",
        path: "/dashboard/driver/orders",
        icon: "📦",
      },

      {
        name: "Active Order",
        path: "/dashboard/driver/active",
        icon: "🚗",
      },

      {
        name: "Trip History",
        path: "/dashboard/driver/history",
        icon: "📜",
      },

      { section: "FINANCE" },

      {
        name: "Wallet",
        path: "/dashboard/driver/wallet",
        icon: "💰",
      },

      {
        name: "Payments",
        path: "/dashboard/driver/payments",
        icon: "💳",
      },

      { section: "PROFILE" },

      {
        name: "Vehicle",
        path: "/dashboard/driver/vehicle",
        icon: "🚘",
      },

      {
        name: "Reviews",
        path: "/dashboard/driver/reviews",
        icon: "⭐",
      },
    ],

    OPS_ADMIN: [
      { name: "Dashboard", path: "/dashboard/ops-admin", icon: "🏠" },

      { section: "VERIFICATION" },

      {
        name: "KYC Verification",
        path: "/dashboard/ops-admin/verification",
        icon: "🪪",
      },

      { section: "DRIVER MANAGEMENT" },

      {
        name: "Driver Applications",
        path: "/dashboard/ops-admin/drivers",
        icon: "👨‍✈️",
      },

      { name: "Vehicles", path: "/dashboard/ops-admin/vehicles", icon: "🚘" },
    ],

    FINANCE_ADMIN: [
      {
        name: "Payments",
        path: "/dashboard/finance-admin/payments",
        icon: "💳",
      },
      { name: "Refunds", path: "/dashboard/finance-admin/refunds", icon: "↩️" },
    ],

    SUPER_ADMIN: [
      { name: "Users", path: "/dashboard/super-admin/users", icon: "👥" },
      { name: "Roles", path: "/dashboard/super-admin/roles", icon: "🛡️" },
      { name: "Config", path: "/dashboard/super-admin/config", icon: "⚙️" },
    ],
  };

  return (
    <aside className="w-64 bg-primary text-white p-6 flex flex-col shadow-lg">
      {/* LOGO */}
      <h1 className="text-xl font-bold mb-6">TAKSOL</h1>

      <p className="text-sm opacity-80 mb-6">{role}</p>

      {/* MENU */}
      <nav className="flex flex-col gap-1 mb-10 overflow-y-auto">
        {menus[role || ""]?.map((m, i) => {
          if (m.section) {
            return (
              <p key={i} className="text-xs opacity-60 mt-4 mb-1">
                {m.section}
              </p>
            );
          }

          const active = pathname.startsWith(m.path);

          return (
            <Link
              key={m.path}
              href={m.path}
              className={`flex items-center gap-2 p-2 rounded transition
                ${active ? "bg-primaryDark" : "hover:bg-primaryDark"}
              `}
            >
              <span>{m.icon}</span>
              <span>{m.name}</span>
            </Link>
          );
        })}
      </nav>

      {/* SWITCH ROLE */}
      {roles.length > 1 && (
        <div className="mt-auto mb-4">
          <p className="text-xs opacity-70 mb-2">Switch Mode</p>

          {roles.map((r) => (
            <button
              key={r}
              onClick={() => {
                setRole(r);

                const roleRouteMap: any = {
                  CUSTOMER: "/dashboard/customer",
                  DRIVER: "/dashboard/driver",
                  OPS_ADMIN: "/dashboard/ops-admin",
                  FINANCE_ADMIN: "/dashboard/finance-admin",
                  SUPER_ADMIN: "/dashboard/super-admin",
                };

                router.push(roleRouteMap[r]);
              }}
              className="block w-full text-left bg-white text-black p-2 rounded mb-2 hover:bg-gray-200"
            >
              {r}
            </button>
          ))}
        </div>
      )}

      {/* LOGOUT */}

      <button
        onClick={() => {
          logout();
          localStorage.clear();

          router.push("/login");
        }}
        className="bg-red-500 p-2 rounded hover:bg-red-600"
      >
        Logout
      </button>
    </aside>
  );
}
