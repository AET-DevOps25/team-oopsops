import axios from "axios";

const authApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/authentication`,
});

export default authApi;