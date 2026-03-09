import "./globals.css";
import { Toaster } from "react-hot-toast";
import { AuthProvider } from "@/context/AuthContext";

export const metadata = {
  title: "TAKSOL",
  description: "Ride Hailing System",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>

        <AuthProvider>
          {children}
        </AuthProvider>

        <Toaster
          position="top-right"
          toastOptions={{
            style: {
              background: "#38bdf8",
              color: "#fff",
            },
          }}
        />

      </body>
    </html>
  );
}