import axios, { AxiosHeaders, InternalAxiosRequestConfig } from "axios";
import { refreshAccessToken } from "@/services/tokenService";

const documentApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/documents`,
});

documentApi.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const raw = localStorage.getItem("token_set");
  if (raw) {
    const token = JSON.parse(raw).accessToken as string;
    const headers = new AxiosHeaders(config.headers);
    headers.set("Authorization", `Bearer ${token}`);
    config.headers = headers;
  }
  return config;
});

documentApi.interceptors.response.use(
  (res) => res,
  async (err) => {
    if (err.response?.status === 401) {
      try {
        const newAccess = await refreshAccessToken();
        const headers = new AxiosHeaders(err.config.headers);
        headers.set("Authorization", `Bearer ${newAccess}`);
        err.config.headers = headers;
        return documentApi(err.config);
      } catch {
        window.location.href = "/login";
      }
    }
    return Promise.reject(err);
  }
);

export default documentApi;