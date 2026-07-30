import api from './api';

export const authService = {
  // Realiza a chamada de login enviando as credenciais esperadas pelo backend (LoginRequest)
  login: async (username, password) => {
    const response = await api.post('/auth/login', { username, password });
    return response.data; // Retorna o AuthResponse que contém o token JWT
  },

  // Realiza o cadastro enviando os dados esperados pelo backend (RegisterRequest)
  register: async (username, password, nome, email) => {
    const response = await api.post('/auth/register', { username, password, nome, email });
    return response.data; // Retorna o AuthResponse que contém o token JWT após o cadastro
  }
};
