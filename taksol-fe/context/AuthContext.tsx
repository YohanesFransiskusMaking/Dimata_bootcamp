"use client";

import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import { jwtDecode } from "jwt-decode";

interface AuthContextType {
  userId: string | null;
  email: string | null;
  role: string | null;
  roles: string[];
  setRole: (role: string) => void;
  isLoading: boolean;
  isAuthenticated: boolean;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [userId, setUserId] = useState<string | null>(null);
  const [email, setEmail] = useState<string | null>(null);
  const [role, setRole] = useState<string | null>(null);
  const [roles, setRoles] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      setIsLoading(false);
      return;
    }

    try {
      const decoded: any = jwtDecode(token);

      setUserId(decoded.sub);
      setEmail(decoded.email);

      const roleArray = Array.isArray(decoded.groups)
        ? decoded.groups
        : [decoded.groups];

      setRoles(roleArray);

      const defaultRole = roleArray[0] || null;
      setRole(defaultRole);

      if (defaultRole) {
        localStorage.setItem("activeRole", defaultRole);
      }
    } catch (err) {
      localStorage.clear();
    }

    setIsLoading(false);
  }, []);

  const logout = () => {
    localStorage.clear();
    window.location.href = "/login";
  };

  return (
    <AuthContext.Provider
      value={{
        userId,
        email,
        role,
        roles,
        setRole,
        isAuthenticated: !!userId,
        isLoading,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }

  return context;
}
