import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import { authService } from '../services/authService';
import { useAuth } from '../hooks/useAuth';
import { useNavigate, Link } from 'react-router-dom';
import AuthLayout from '../components/layout/AuthLayout';

function Login() {
  const [username, setUsername] = useState('user');
  const [password, setPassword] = useState('user123');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError('');
      const res = await authService.login(username, password);

      // Salva as credenciais no estado global (AuthContext)
      login(res.token, username);
      // Redireciona o usuário para o Dashboard (raiz)
      navigate('/');
    } catch (error) {
      console.error(error);
      setError('Username ou senha incorretos');
    } finally {
      setLoading(false);
    }
  }

  /*
    flex = flexbox 
    items-center, justify-center = centralizar verticalmente e horizontalmente
    min-h screen = a altura da div vai ser de no mínimo 100% da altura da tela
    p-4 = paddind interno de 4px
    bg-slate-100 = cor de fundo da div (cinza claro)
    w-full max-w-md = a largura da div vai ser de no máximo 100% da tela 
    p-8 bg-white rounded-lg shadow-md = padding interno de 8px, cor de fundo branca, bordas arredondadas e sombra

    focus = o focus é um estado que é ativado quando um usuário clica ou navega para dentro do campo, 
    e os estilos aplicados dentro de focus são os estilos que serão aplicados no campo quando ele estiver em foco,
    no caso, focus:outline-none remove a borda padrão do campo, e focus:ring-2 focus:ring-blue-500 adiciona
    uma borda azul de 2px quando o campo estiver em foco.
    text-sm = text size small, dimensão do texto para 14px
    mb-2 = margin bottom de 2*4px=8px, adiciona um espaço no fundo do elemento.
    */
  return (
    <AuthLayout>
      <form onSubmit={handleLogin}>
        <h2 className="text-2xl font-bold mb-6 text-center text-slate-800 dark:text-slate-100 transition-colors">Login</h2>

        {error && <p className='text-red-750 dark:text-red-400 mb-4 text-sm text-center bg-red-50 dark:bg-red-950/20 border border-red-200 dark:border-red-900/50 p-2 rounded transition-colors'>{error}</p>}

        <div className="mb-5 p-3 bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-800/60 rounded-lg text-xs text-slate-700 dark:text-slate-300">
          <p className="text-sm font-semibold text-blue-900 dark:text-blue-300 mb-1 flex items-center gap-1">
            💡 Credenciais de Teste (Seeder no BD)
          </p>
          <p className="text-sm text-slate-600 dark:text-slate-400 mb-2">
            Esta aplicação contém um usuário padrão cadastrado via seeder no banco de dados:
          </p>
          <div className="flex items-center justify-between bg-white dark:bg-slate-950 px-3 py-1.5 rounded border border-blue-100 dark:border-blue-900/50 font-mono text-xs text-slate-800 dark:text-slate-200">
            <div><span className="text-sm text-slate-500 dark:text-slate-400 font-sans">Usuário:</span> <strong className="text-blue-600 dark:text-blue-400">user</strong></div>
            <div><span className="text-sm text-slate-500 dark:text-slate-400 font-sans">Senha:</span> <strong className="text-blue-600 dark:text-blue-400">user123</strong></div>
          </div>
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
            placeholder="user"
            required
          />
        </div>

        <div className='mb-6'>
          <label className="block text-slate-700 dark:text-slate-300 text-sm font-semibold mb-2 transition-colors">
            Senha
          </label>
          <div className="relative">
            {/* relative faz com que o botão de mostrar senha fique dentro do input
                  um elemento relative serve como ponto de origem para os elementos filhos
                  absolutos, nesse caso o botão é absolute então ele vai ser posicionado em relação ao seu pai relative.
                  Ou seja, ele usará dos limites impostos pelo pai (relative)
              */}
            <input
              type={showPassword ? "text" : "password"}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className='w-full px-3 py-2 border border-slate-300 dark:border-slate-800 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 pr-10 transition-colors'
              placeholder="user123"
              required
            />
            {/* pr-10 = padding right de 10px, coloca um espaçamento interno no lado direito do input.
                  Usado para que o icone do olho não fique colado no texto
              */}
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
          {loading ? 'Conectando...' : 'Entrar'}
        </button>

        <p className="mt-4 text-sm text-center text-slate-600 dark:text-slate-400 transition-colors">
          Não tem uma conta?{' '}
          <Link to="/register" className="text-blue-500 hover:underline font-semibold">
            Cadastre-se
          </Link>
        </p>
      </form>
    </AuthLayout>
  );
}

export default Login;