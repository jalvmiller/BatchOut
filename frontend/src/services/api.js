import axios from 'axios';

// Cria uma única instância do Axios que vai ser usada por todos os services
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
});

// Interceptador de Requisição: roda antes de enviar qualquer chamada HTTP
api.interceptors.request.use(
  (config) => {
    // Busca o token salvo no localStorage
    const token = localStorage.getItem('token');

    // Se o token existir, adiciona ele no cabeçalho Authorization
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },

  (error) => {
    return Promise.reject(error);
  }
);

export default api;
