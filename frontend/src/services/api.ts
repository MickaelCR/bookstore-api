// src/services/api.ts
import axios from 'axios';

// URL de base de votre backend (JCloud ou Localhost)
const API_URL = 'http://localhost:8080/api'; 
// const API_URL = 'http://113.198.66.75:10148/api'; // Pour la prod

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
    }
    return Promise.reject(error);
  }
);

export default api;