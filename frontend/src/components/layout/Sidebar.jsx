// Ícones
import { X, LogOut, Home, BookOpen, Settings } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { Link } from 'react-router-dom';

// Sidebar fica na lateral esquerda
// Recebe isOpen para saber se está aberta (em mobile) e onClose para fechar, PROPS
function Sidebar({ isOpen, onClose }) {

    const { logout } = useAuth();

    // <> significa que retorna um fragmento, vão ser dois blocos
    return (
        <>
            {/* isOpen, só renderiza se a sidebar estiver aberta
                fixed         -> fica fixo na tela, mesmo se usar scrollbar
                inset-0       -> posiciona top/right/bottom/left em 0, ocupa a tela
                bg-black      -> fundo preto
                bg-opacity-50 -> opacidade
                lg:hidden     -> escondido em tela grande (lg = large)
                z-30          -> coloca o overlay acima do conteúdo normal,
                fica abaixo da sidebar (z-40)     
            */}
            {/* Overlay (fundo escuro) que aparece em mobile quando sidebar está aberta */}
            {isOpen && (
                <div
                    className="fixed inset-0 bg-black bg-opacity-50 lg:hidden z-30"
                    onClick={onClose}
                />
            )}

            {/* A sidebar em si */}
            {/* 
                aside é só semantica
                fixed         -> fixado como antes, acompanha a scrollbar
                lg:static     -> em tela grande (lg), a sidebar se comporta
                como um elmento normal
                left-0        -> posiciona no canto esquerdo
                top-0         -> combinando com o left, posiciona no superior esquerdo
                h-screen      -> fundo cinza escuro
                text-white    -> texto branco
                shadow-lg     -> sombra ao redor

                transform transition-transform duration-300
                -> habilita transformação com animação de 300ms

                z-40              -> sidebar fica acima do overlay
                ${ isOpen ..      -> uma expressão, se for verdade
                translate-x-0    -> se verdade, não move no eixo X
                translate-x-full -> se não for verdade
                desloca 100% da largura para a esquerda
                
                lg:translate-x-0  -> se for tela grande, nunca fica escondida                 
            */}
            <aside
                className={`fixed lg:static left-0 top-0 h-screen w-64 bg-slate-900 text-white shadow-lg transform transition-transform duration-300 z-40 ${isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}`}
            >
                {/* Header da sidebar com botão de fechar (apenas mobile) */}
                {/* 
                    flex items-center justify-between -> alinhamento e espaçamento
                    p-4                               -> padding
                    border-b border-slate-700         -> borda bottom,
                    chama onClose para fechar
                    o X é o ícone de fechar
                */}
                <div className="flex items-center justify-between p-4 border-b border-slate-700 lg:hidden">
                    <h2 className="text-xl font-bold">Menu</h2>
                    <button
                        onClick={onClose}
                        className="p-2 hover:bg-slate-800 rounded"
                        aria-label="Fechar menu"
                    >
                        <X size={20} />
                    </button>
                </div>

                {/* Lista de navegação com os links: Dashboard - Questões - Configurações */}
                {/* space-y-2                    -> espaçamento vertical entre todos os elementos 
                    flex items-center gap-3      -> coloca tudo na mesma linha, gap é espaçamento entre ícone e texto
                    hover:bg-blue-600 transition -> muda a cor do fundo ao passar o mouse, transição suave de cor                
                */}
                <nav className="p-4 space-y-2">
                    <Link
                        to="/"
                        className="flex items-center gap-3 px-4 py-3 rounded hover:bg-blue-600 dark:hover:bg-slate-800 transition"
                    >
                        <Home size={20} />
                        <span>Dashboard</span>
                    </Link>
                    <Link
                        to="/"
                        className="flex items-center gap-3 px-4 py-3 rounded hover:bg-blue-600 dark:hover:bg-slate-800 transition"
                    >
                        <BookOpen size={20} />
                        <span>Questões</span>
                    </Link>
                    <Link
                        to="/configuracoes"
                        className="flex items-center gap-3 px-4 py-3 rounded hover:bg-blue-600 dark:hover:bg-slate-800 transition"
                    >
                        <Settings size={20} />
                        <span>Configurações</span>
                    </Link>
                    <button
                        onClick={logout}
                        className="flex items-center w-full gap-3 px-4 py-3 rounded
                        hover:bg-red-600/20 hover:text-red-400 text-slate-400 transition
                        text-left mt-auto font-medium"
                    >
                        <LogOut size={20} />
                        <span>Sair</span>
                    </button>
                </nav>
            </aside>
        </>
    );
}

export default Sidebar;
