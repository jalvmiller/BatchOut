import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import { authService } from '../services/authService';
import { useAuth } from '../hooks/useAuth';
import { useNavigate, Link } from 'react-router-dom';
import AuthLayout from '../components/layout/AuthLayout';

function Register() {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError('');
      const res = await authService.register(username, password, nome, email);

      // Salva as credenciais recebidas no cadastro no estado global
      login(res.token, username);
      // Redireciona o usuário para o Dashboard (raiz)
      navigate('/');
    } catch (error) {
      console.error(error);
      setError('Erro ao realizar o cadastro. Tente outro username.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout>
      <form onSubmit={handleRegister}>
        <h2 className="text-2xl font-bold mb-6 text-center text-slate-800 dark:text-slate-100 transition-colors">Cadastro</h2>

        {error && <p className='text-red-750 dark:text-red-400 mb-4 text-sm text-center bg-red-50 dark:bg-red-950/20 border border-red-200 dark:border-red-900/50 p-2 rounded transition-colors'>{error}</p>}

        <div className='mb-4'>
          <label className="block text-slate-700 dark:text-slate-300 text-sm font-semibold mb-2 transition-colors">
            Nome Completo
          </label>
          <input
            type="text"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            className='w-full px-3 py-2 border border-slate-300 dark:border-slate-800 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors'
            placeholder="Inserir seu nome"
            required
          />
        </div>

        <div className='mb-4'>
          <label className="block text-slate-700 dark:text-slate-300 text-sm font-semibold mb-2 transition-colors">
            E-mail
          </label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className='w-full px-3 py-2 border border-slate-300 dark:border-slate-800 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors'
            placeholder="Inserir seu e-mail"
            required
          />
        </div>

        <div className='mb-4'>
          <label className="block text-slate-700 dark:text-slate-300 text-sm font-semibold mb-2 transition-colors">
            Username
          </label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className='w-full px-3 py-2 border border-slate-300 dark:border-slate-800 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors'
            placeholder="Inserir seu username"
            required
          />
        </div>

        <div className='mb-6'>
          <label className="block text-slate-700 dark:text-slate-300 text-sm font-semibold mb-2 transition-colors">
            Senha
          </label>
          <div className="relative">
            <input
              type={showPassword ? "text" : "password"}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className='w-full px-3 py-2 border border-slate-300 dark:border-slate-800 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 pr-10 transition-colors'
              placeholder="Inserir sua senha"
              required
            />
            <button
              type="button"
              className='absolute inset-y-0 right-0 pr-3 flex items-center text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-300 cursor-pointer transition-colors'
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </button>
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className='w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600 disabled:bg-blue-300 transition duration-200 cursor-pointer'
        >
          {loading ? 'Cadastrando...' : 'Cadastrar'}
        </button>

        <p className="mt-4 text-sm text-center text-slate-600 dark:text-slate-400 transition-colors">
          Já tem uma conta?{' '}
          <Link to="/login" className="text-blue-500 hover:underline font-semibold">
            Faça login
          </Link>
        </p>
      </form>
    </AuthLayout>
  );
}

export default Register;
