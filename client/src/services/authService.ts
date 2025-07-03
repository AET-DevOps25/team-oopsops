import { RegistrationRequest } from "@/types/registration";
import axios from "axios";

const authApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/authentication`,
});

export async function registerUser(data: RegistrationRequest): Promise<void> {
  await authApi.post("/register", data, {
    headers: { "Content-Type": "application/json" },
  });
}

export async function loginUser(data: { username: string; password: string }): Promise<string> {
  const params = new URLSearchParams();
  params.append("client_id", "oopsops-backend");
  params.append("client_secret", import.meta.env.BACKEND_SECRET);
  params.append("username", data.username);
  params.append("password", data.password);
  params.append("grant_type", "password");

  const response = await authApi.post("/realms/oopsops/protocol/openid-connect/token", params, {
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
  });

  return response.data.access_token;
}
