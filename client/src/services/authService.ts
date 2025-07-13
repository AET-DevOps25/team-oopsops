import authApi from "@/api/authApi";
import type { RegistrationRequest } from "@/types/registration";

export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  refresh_expires_in: number;
}

export async function registerUser(data: RegistrationRequest): Promise<void> {
  await authApi.post("/register", data);
}

export async function loginUser(data: { username: string; password: string }): Promise<TokenResponse> {
  const resp = await authApi.post<TokenResponse>("/login", data);
  return resp.data;
}

export async function refreshToken(refreshToken: string): Promise<TokenResponse> {
  const resp = await authApi.post<TokenResponse>("/refresh", { refresh_token: refreshToken });
  return resp.data;
}
