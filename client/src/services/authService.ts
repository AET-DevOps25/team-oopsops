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
