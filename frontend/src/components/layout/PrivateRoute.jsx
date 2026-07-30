import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

// Componente wrapper para proteger rotas privadas
function PrivateRoute({ children }) {
  const { isAuthenticated } = useAuth();

  // Se o usuário estiver autenticado, renderiza os filhos (Dashboard, Layout, etc.)
  // Caso contrário, redireciona para a tela de login
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}

export default PrivateRoute;
