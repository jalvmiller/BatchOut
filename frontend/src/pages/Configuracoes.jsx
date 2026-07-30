import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useUser } from '../hooks/useUser';
import { getMediaUrl } from '../utils/urlUtils';
import { Moon, Sun, User, Award, Shield, Pencil } from 'lucide-react';

function Configuracoes() {
    const { user } = useAuth();
    const [escuro, setEscuro] = useState(() => document.documentElement.classList.contains('dark'));
    const { fileInputRef, abrirSeletorDeArquivo, handleFileChange } = useUser();

    // O toggle funciona baseado no localStorage e na classe dark no elemento raiz
    // do documento html para que o tailwind faça as alterações automáticas
    // e o tema seja salvo e persistido entre as sessões.
    // O "theme" é uma chave comum criada com esse método que serve só para guardar o valor.
    const toggleTema = () => {
        const novoEstado = !escuro;
        setEscuro(novoEstado);
        if (novoEstado) {
            document.documentElement.classList.add('dark');
            localStorage.setItem('theme', 'dark');
        } else {
            document.documentElement.classList.remove('dark');
            localStorage.setItem('theme', 'light');
        }
    };

    // Método para puxar a role do usuário e retornar o nome legível,
    // para ser exibido no card.
    // O if (user?.X) significa: Se o usuário existir e tiver a propriedade X
    // É um shorthand do if (user !== null && user.X !== null) etc
    const obterRoleLabel = () => {
        if (user?.administrador) return 'Administrador';
        if (user?.especialista) return 'Especialista';
        return 'Usuário Comum';
    };

    const avatarUrl = getMediaUrl(user?.avatar);

    return (
        <div className="max-w-4xl mx-auto p-4 space-y-6">
            <h2 className="text-3xl font-bold text-slate-900 dark:text-slate-100 transition-colors">
                Configurações
            </h2>

            {/* CARD: Dados do Perfil */}
            <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-lg border border-slate-100 dark:border-slate-800 p-6 space-y-6 transition-colors duration-300">
                {/* Avatar + Opção para Editar */}
                <div className="flex items-center gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
                    <div
                        onClick={abrirSeletorDeArquivo}
                        className="relative group w-20 h-20 rounded-full cursor-pointer flex-shrink-0 overflow-hidden shadow-md ring-2 ring-blue-500/20 dark:ring-blue-400/20"
                        title="Clique para alterar a foto de perfil"
                    >
                        {avatarUrl ? (
                            <img
                                src={avatarUrl}
                                alt={user?.nome || 'Avatar'}
                                className="w-full h-full object-cover"
                            />
                        ) : (
                            <div className="w-full h-full bg-gradient-to-br from-blue-100 to-blue-200 dark:from-blue-900/20 dark:to-slate-800 text-blue-600 dark:text-blue-400 flex items-center justify-center">
                                <User size={40} />
                            </div>
                        )}

                        {/* Overlay Translúcido com Ícone de Lápis (Visível no Hover) */}
                        <div className="absolute inset-0 bg-slate-900/60 flex flex-col items-center justify-center text-white opacity-0 group-hover:opacity-100 transition-opacity duration-200 backdrop-blur-[2px]">
                            <Pencil size={20} className="drop-shadow" />
                            <span className="text-[10px] font-semibold tracking-wide uppercase mt-0.5">Editar</span>
                        </div>
                        {/* Input de arquivo invisível */}
                        <input
                            type="file"
                            ref={fileInputRef}
                            onChange={handleFileChange}
                            accept="image/*"
                            className="hidden"
                        />
                    </div>
                    <div>
                        <h3 className="text-xl font-bold text-slate-800 dark:text-slate-100">
                            {user?.nome || user?.username}
                        </h3>
                        <p className="text-sm text-slate-500 dark:text-slate-400">
                            Clique na imagem para alterar sua foto de perfil
                        </p>
                    </div>
                </div>

                {/* Dados da Conta */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-2">
                    <div>
                        <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-400 mb-1">
                            Nome Completo
                        </label>
                        <p className="text-slate-800 dark:text-slate-200 font-semibold text-base">
                            {user?.nome || 'Não informado'}
                        </p>
                    </div>

                    <div>
                        <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-400 mb-1">
                            Nome de Usuário
                        </label>
                        <p className="text-slate-800 dark:text-slate-200 font-semibold text-base">
                            @{user?.username}
                        </p>
                    </div>

                    <div className="flex items-center gap-2">
                        <div className="bg-amber-100 dark:bg-amber-900/30 text-amber-600 dark:text-amber-400 p-2 rounded-lg">
                            <Award size={20} />
                        </div>
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-400">
                                Pontuação
                            </label>
                            <p className="text-slate-800 dark:text-slate-200 font-bold text-base">
                                {user?.pontos ?? 0} pontos
                            </p>
                        </div>
                    </div>

                    <div className="flex items-center gap-2">
                        <div className="bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400 p-2 rounded-lg">
                            <Shield size={20} />
                        </div>
                        <div>
                            <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 dark:text-slate-400">
                                Perfil de Acesso (Role)
                            </label>
                            <span className={`inline-block text-xs font-extrabold uppercase px-2 py-0.5 rounded border mt-1 ${user?.administrador
                                ? 'bg-red-50 dark:bg-red-950/20 text-red-700 dark:text-red-400 border-red-200 dark:border-red-900/40'
                                : user?.especialista
                                    ? 'bg-purple-50 dark:bg-purple-950/20 text-purple-700 dark:text-purple-400 border-purple-200 dark:border-purple-900/40'
                                    : 'bg-slate-50 dark:bg-slate-800/40 text-slate-700 dark:text-slate-400 border-slate-200 dark:border-slate-700'
                                }`}>
                                {obterRoleLabel()}
                            </span>
                        </div>
                    </div>
                </div>
            </div>


            {/* CARD 2: Tema Escuro */}
            <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-lg border border-slate-100 dark:border-slate-800 p-6 space-y-6 transition-colors duration-300">
                <div className="flex items-center gap-4 border-b border-slate-100 dark:border-slate-800 pb-4">
                    <div className="bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 p-3 rounded-full">
                        {escuro ? <Moon size={28} /> : <Sun size={28} />}
                    </div>
                    <div>
                        <h3 className="text-lg font-bold text-slate-800 dark:text-slate-100">Preferências Visuais</h3>
                        <p className="text-sm text-slate-500 dark:text-slate-400">Personalize a aparência do sistema</p>
                    </div>
                </div>

                <div className="flex items-center justify-between py-2">
                    <div>
                        <h4 className="font-semibold text-slate-800 dark:text-slate-200">Tema Escuro (Night Theme)</h4>
                        <p className="text-xs text-slate-400 dark:text-slate-500">
                            Ajusta as cores para reduzir o cansaço visual.
                        </p>
                    </div>

                    {/* Botão Switch com animação fluida */}
                    <button
                        onClick={toggleTema}
                        className={`w-14 h-8 flex items-center rounded-full p-1 cursor-pointer transition-colors duration-300 ${escuro ? 'bg-blue-600' : 'bg-slate-300 dark:bg-slate-800'
                            }`}
                        aria-label="Alternar tema"
                    >
                        <div
                            className={`bg-white w-6 h-6 rounded-full shadow-md transform transition-transform duration-300 flex items-center justify-center ${escuro ? 'translate-x-6' : 'translate-x-0'
                                }`}
                        >
                            {escuro ? (
                                <Moon size={14} className="text-blue-600" />
                            ) : (
                                <Sun size={14} className="text-amber-500" />
                            )}
                        </div>
                    </button>
                </div>
            </div>
        </div >
    );
}

export default Configuracoes;