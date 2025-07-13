import axios from 'axios';

const genaiApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/genai`,
});

export default genaiApi;