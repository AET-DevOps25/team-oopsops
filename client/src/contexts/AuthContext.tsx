import React, {
  createContext,
  useState,
  useEffect,
  useContext,
  ReactNode,
} from "react";
import { jwtDecode } from "jwt-decode";
import { loginUser, refreshToken } from "@/services/authService";
import type { User, DecodedToken } from "@/types/auth";

type TokenSet = {
  accessToken: string;
  refreshToken: string;
  expiresAt: number;
};

type AuthContextType = {
  user: User | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  getAccessToken: () => string | null;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {

  const saved = localStorage.getItem("token_set");
  let initTokens: TokenSet | null = null;
  let initUser: User | null = null;
  if (saved) {
    try {
      const t = JSON.parse(saved) as TokenSet;
      const decoded = jwtDecode<DecodedToken>(t.accessToken);
      initTokens = t;
      initUser = {
        username: decoded.preferred_username,
        email: decoded.email,
      };
    } catch {
      localStorage.removeItem("token_set");
    }
  }

  const [tokens, setTokens] = useState<TokenSet | null>(initTokens);
  const [user, setUser]     = useState<User | null>(initUser);

  useEffect(() => {
    if (!tokens) return;
    const msUntil = tokens.expiresAt - Date.now() - 30_000;
    if (msUntil <= 0) {
      doRefresh();
    } else {
      const id = setTimeout(doRefresh, msUntil);
      return () => clearTimeout(id);
    }
  }, [tokens]);

  const login = async (username: string, password: string) => {
    const tr = await loginUser({ username, password });
    const expMs = Date.now() + tr.expires_in * 1000;
    const newTokens: TokenSet = {
      accessToken: tr.access_token,
      refreshToken: tr.refresh_token,
      expiresAt: expMs,
    };
    localStorage.setItem("token_set", JSON.stringify(newTokens));
    setTokens(newTokens);

    const decoded = jwtDecode<DecodedToken>(tr.access_token);
    setUser({ username: decoded.preferred_username, email: decoded.email });
  };

  const logout = () => {
    localStorage.removeItem("token_set");
    setTokens(null);
    setUser(null);
  };

  const doRefresh = async () => {
    if (!tokens) return logout();
    try {
      const tr = await refreshToken(tokens.refreshToken);
      const expMs = Date.now() + tr.expires_in * 1000;
      const newTokens: TokenSet = {
        accessToken: tr.access_token,
        refreshToken: tr.refresh_token,
        expiresAt: expMs,
      };
      localStorage.setItem("token_set", JSON.stringify(newTokens));
      setTokens(newTokens);
    } catch {
      logout();
    }
  };

  const getAccessToken = () => tokens?.accessToken ?? null;

  return (
    <AuthContext.Provider value={{
      user,
      isAuthenticated: Boolean(user),
      login,
      logout,
      getAccessToken
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be inside AuthProvider");
  return ctx;
};
