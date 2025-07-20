import axios from 'axios';

const chatApi = axios.create({
  // TODO: For local development testing, replace VITE_API_URL with 'http://localhost:8000'
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/genai`,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default chatApi;
