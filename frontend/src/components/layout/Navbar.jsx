// import do ícone
import { Menu, LogOut, User } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { getMediaUrl } from '../../utils/urlUtils';

// Navbar fica no topo da página
// Função que recebe prop onMenuClick, quando alguém clica no botão do menu
// chama essa função
function Navbar({ onMenuClick }) {

	const { user, logout } = useAuth();
	// sem destruct; retorna uma String
	const avatarUrl = getMediaUrl(user?.avatar);

	return (
		// bg blue 600 -> fundo azul
		// text white  -> texto branco
		// shadow lg   -> sombra embaixo da barra
		<nav className="bg-blue-600 dark:bg-slate-900 text-white shadow-lg border-b border-transparent dark:border-slate-800 transition-colors duration-300">

			{/*
            max-w-7xl       -> largura máxima
            mx-auto         -> centralizar horizontalmente
            px-4            -> espaçamento horizontal interno 1 rem no eixo x
            py-4            -> espaçamento vertical interno de 1 rem no eixo y
            flex            -> flexbox para alinhar elementos na linha
            items-center    -> alinhar verticalmente ao centro
            justify-between -> justificar na linha, espaçar usando os extremos 
         */}
			<div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
				{/* 
            Botão de menu para mobile
            chama a função quando é clicado
            aria-label        ->"Abrir Menu", acessibilidade para leitor de tela
            lg:hidden         -> esconde o botão quando tem tela grande(lg)
            já que em tela grande geralmente a sidebar fica visível e não precisa de nav
            p-2               -> padding de 0.5 rem no botão
            hover:bg-blue-700 -> quando passa o mouse, muda para azul mais escuro
            rounded só deixa as bordas arredondadas
        */}
				<button
					onClick={onMenuClick}
					className="lg:hidden p-2 hover:bg-blue-700 dark:hover:bg-slate-800 rounded"
					aria-label="Abrir menu"
				>
					<Menu size={24} />
				</button>

				{/* Logo/Titulo */}
				<div className="flex items-center gap-2">
					<h1 className="text-2xl font-extrabold tracking-wider bg-gradient-to-r from-white
					via-blue-100 to-cyan-200 bg-clip-text text-transparent">
						PROBEND
					</h1>
				</div>

				{/* Botão de login/logout */}
				{user ? (
					<div className="flex items-center gap-4">
						<div className="flex items-center justify-between min-w-[12rem] bg-blue-700/50 dark:bg-slate-800/80 pl-1.5 
							pr-4 py-1 rounded-full border border-blue-400/20 dark:border-slate-700 shadow-inner">
							<div className="w-10 h-10 rounded-full overflow-hidden bg-blue-500/30 dark:bg-slate-700 flex items-center
							justify-center text-white ring-2 ring-white/20 flex-shrink-0">
								{avatarUrl ? (
									<img
										src={avatarUrl}
										alt={user.nome || "Usuário"}
										className="w-full h-full object-cover"
									/>
								) : (
									<User size={16} className="text-blue-100 dark:text-slate-200" />
								)}
							</div>

							<div>
								<span className="text-sm font-medium hidden sm:inline max-w-[8.5rem] truncate">
									Olá, {user.nome}
								</span>
							</div>
						</div>

						<button
							onClick={logout}
							className="flex items-center gap-1.5 
							text-sm bg-blue-700 hover:bg-blue-800 dark:bg-slate-800 dark:hover:bg-slate-700
							px-3 py-2 rounded-lg transition font-semibold shadow-sm"
						>
							<LogOut size={16} />
							<span>
								Sair
							</span>
						</button>
					</div>
				) : (
					<div className='w-10' />
				)}
				{/* Espaço vazio colocado na direita da navbar para balancear */}
			</div>
		</nav>
	);
}

export default Navbar;