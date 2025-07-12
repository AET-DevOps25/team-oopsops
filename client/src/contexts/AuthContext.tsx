import React, {
  createContext,
  useState,
  useContext,
  ReactNode,
} from "react";
import { jwtDecode } from "jwt-decode";
import type { User, DecodedToken } from "@/types/auth";

type AuthContextType = {
  user: User | null;
  login: (token: string) => void;
  logout: () => void;
  isAuthenticated: boolean;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  // Initialize from localStorage synchronously
  const [user, setUser] = useState<User | null>(() => {
    const token = localStorage.getItem("access_token");
    if (!token) return null;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return {
        username: decoded.preferred_username,
        email: decoded.email,
      };
    } catch {
      localStorage.removeItem("access_token");
      return null;
    }
  });

  const login = (token: string) => {
    localStorage.setItem("access_token", token);
    const decoded = jwtDecode<DecodedToken>(token);
    setUser({
      username: decoded.preferred_username,
      email: decoded.email,
    });
  };

  const logout = () => {
    localStorage.removeItem("access_token");
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{ user, login, logout, isAuthenticated: Boolean(user) }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be inside AuthProvider");
  return ctx;
};
