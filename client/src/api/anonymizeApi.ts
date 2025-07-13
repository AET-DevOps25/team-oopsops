import axios, { AxiosHeaders, InternalAxiosRequestConfig } from "axios";
import { refreshAccessToken } from "@/services/tokenService";

const anonymizeApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/anonymization`,
});

anonymizeApi.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const raw = localStorage.getItem("token_set");
  if (raw) {
    const { accessToken } = JSON.parse(raw) as { accessToken: string };
    const headers = new AxiosHeaders(config.headers);
    headers.set("Authorization", `Bearer ${accessToken}`);
    config.headers = headers;
  }
  return config;
});

anonymizeApi.interceptors.response.use(
  res => res,
  async err => {
    if (err.response?.status === 401) {
      try {
        const newAccess = await refreshAccessToken();
        const headers = new AxiosHeaders(err.config.headers);
        headers.set("Authorization", `Bearer ${newAccess}`);
        err.config.headers = headers;
        return anonymizeApi(err.config);
      } catch {
        window.location.href = "/login";
      }
    }
    return Promise.reject(err);
  }
);

export default anonymizeApi;