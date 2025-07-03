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
  const response = await authApi.post("/login", data, {
    headers: { "Content-Type": "application/json" },
  });

  return response.data.access_token;
}
