import { refreshToken } from "@/services/authService";

export interface TokenSet {
  accessToken: string;
  refreshToken: string;
  expiresAt: number;
}

export async function refreshAccessToken(): Promise<string> {
  const raw = localStorage.getItem("token_set");
  if (!raw) throw new Error("No token_set in storage");

  const { refreshToken: rt } = JSON.parse(raw) as TokenSet;
  const tr = await refreshToken(rt);

  // Compute new expiry (ms since epoch)
  const expMs = Date.now() + tr.expires_in * 1000;

  const newTokens: TokenSet = {
    accessToken: tr.access_token,
    refreshToken: tr.refresh_token,
    expiresAt: expMs,
  };

  localStorage.setItem("token_set", JSON.stringify(newTokens));
  return newTokens.accessToken;
}
