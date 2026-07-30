import { createContext, useState, useEffect } from 'react';
import api from '../services/api';
// Importa a instância do Axios configurada
// com interceptor de requisições

// O contexto em si. Ele servirá como a "nuvem" de dados.
export const AuthContext = createContext(null);

// Provider, é o componente que gerencia os estados
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchCurrentUser = async (jwtToken) => {
    try {
      const response = await api.get('/auth/me');
      setUser(response.data);
      // Guarda o objeto usuário completo
    } catch (err) {
      console.error("Erro ao buscar os dados do usuário logado", err);
      logout();
    }
  };

  // O useEffect roda uma única vez quando a aplicação inicia no navegador
  useEffect(() => {
    // Busca se existe um token salvo no localStorage do navegador
    const savedToken = localStorage.getItem('token');

    if (savedToken) {
      setToken(savedToken);
      fetchCurrentUser(savedToken).finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  // Função disparada no login bem-sucedido
  const login = async (jwtToken, username) => {
    localStorage.setItem('token', jwtToken);
    setToken(jwtToken);

    // Busca pós-login para retornar dados do usuário
    await fetchCurrentUser(jwtToken);
  };

  // Função disparada no logout
  const logout = async () => {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        // Chama o endpoint de logout enviando o JWT no header
        await api.post('/auth/logout', {}, {
          headers: { Authorization: `Bearer ${token}` }
        });
      }
    } catch (error) {
      console.error("Erro ao fazer logout", error);
    } finally {
      // Apaga do armazenamento local do navegador
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
  };


  // Enquanto estiver verificando o localStorage, mostrar um loading na tela
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-slate-100">
        <p className="text-slate-600 font-semibold">Carregando sessão...</p>
      </div>
    );
  }

  // Retornar o Provider passando os dados e funções que qualquer componente filho poderá usar
  // o !!token é uma forma de converter o token em boolean, ou seja, se o token existir
  // ele será true, se não existir ele será false
  return (
    <AuthContext.Provider value={{ user, setUser, token, isAuthenticated: !!token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}