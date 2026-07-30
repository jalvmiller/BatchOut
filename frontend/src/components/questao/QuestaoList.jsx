import { ChevronLeft, ChevronRight } from 'lucide-react';
import { useState, useEffect, useRef } from 'react';
import QuestaoCard from './QuestaoCard';

const ITEMS_PER_PAGE = 10;

function QuestaoList({
	listaQuestoes,
	carregando,
	erro,
	onExcluir,
	onEditarSucesso,
	meusUpvotes
}) {
	const [paginaAtual, setPaginaAtual] = useState(1);
	// prevLengthRef faz uso do conceito de useRef 
	// useRef é um hook, serve para o React criar um objeto JS
	// básico com a estrutura { current: valorInicial } 
	// para mudar esse valor, deve-se usar current de forma obrigatória

	// ao contrário de um elemento com estado useState, quando o elemento
	// usa o useRef, quando você atualiza a propriedade .current,
	// o elemento não é renderizado novamente.

	// - const prevLength = prevLengthRef.current;
	// - prevLengthRef.current = listaQuestoes.length

	// quando o listaQuestoes é alterado, o useEffect dá trigger
	// 1. primeiro o valor dentro de prevLengthRef.current
	// é guardado na variável local prevLength
	// 2. esse é o tamanho ANTES da alteração
	// 3. depois, o prevLengthRef.current é atualizado
	// com o tamanho de listaQuestoes.length..
	// 4. esse é o valor "anterior" na próxima vez
	// que o efeito acontecer
	const prevLengthRef = useRef(listaQuestoes.length);

	// Quando a quantidade de questões mudar
	useEffect(() => {
		const prevLength = prevLengthRef.current;
		prevLengthRef.current = listaQuestoes.length;

		const totalPages = Math.ceil(listaQuestoes.length / ITEMS_PER_PAGE);

		if (listaQuestoes.length > prevLength) {
			// Se uma nova questão foi adicionada, vai para a página 1 
			setPaginaAtual(1);
		} else if (paginaAtual > totalPages && totalPages > 0) {
			// Se um item foi excluído e a página atual ficou vazia, volta uma página
			setPaginaAtual(totalPages);
		}
	}, [listaQuestoes.length, paginaAtual]);

	// Paginação
	// totalPages usa da divisão do tamanho da lista pela quantidade de itens da página
	// Math.ceil() arredonda o resultado para cima
	const totalPages = Math.ceil(listaQuestoes.length / ITEMS_PER_PAGE);

	// Implementa a lógica de reticências (...) na paginação,
	// serve para evitar que layout quebre caso existam muitas páginas
	// exibe apenas as que estão próximas a atual.. e oculta o resto
	// 1. Se o total for pequeno (<= 7) ela só gera um array linear 1 até o final
	// 2. Se o total for maior, ela verifica em qual página está,
	// caso esteja (<=4) mostra as primeiras 5 páginas individualmente,
	// e pula para o final com totalPages
	//   2.1 Caso esteja perto do final, mostra a primeira
	//   e a quarta última página e depois vai até o final.
	//   2.2 Caso esteja em algum lugar do meio, mostra a primeira
	//   duas do meio e o final. 
	const getPageNumbers = () => {
		const pages = [];
		if (totalPages <= 7) {
			for (let i = 1; i <= totalPages; i++) {
				pages.push(i);
			}
		} else {
			if (paginaAtual <= 4) {
				pages.push(1, 2, 3, 4, 5, '...', totalPages);
			} else if (paginaAtual >= totalPages - 3) {
				pages.push(1, '...', totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1, totalPages);
			} else {
				pages.push(1, '...', paginaAtual - 1, paginaAtual, paginaAtual + 1, '...', totalPages);
			}
		}
		return pages;
	};

	// ITEMS_PER_PAGE = 10
	// pagina 1 (paginaAtual = 1)
	// (1 - 1) * 10 = 0
	// listaQuestoes.slice(0, 1*10=10)
	// Extrai os itens do índice 0 até o 9

	// pagina 2 (paginaAtual = 2)
	// (2 - 1) * 10 = 10
	// listaQuestoes.slice(10, 2*10=20)
	// extrai os itens do índice 10 até o 19
	const questoesPaginadas = listaQuestoes.slice(
		(paginaAtual - 1) * ITEMS_PER_PAGE,
		paginaAtual * ITEMS_PER_PAGE
	);

	return (
		<div className="space-y-6">
			{/* Bloco contendo a lista de questões */}
			<div className="bg-white dark:bg-slate-900 rounded-lg shadow-md p-6 border border-transparent dark:border-slate-800 transition-colors">
				{/* Cabeçalho do bloco */}
				<div className="mb-6 pb-4 border-b border-slate-200 dark:border-slate-800">
					<h3 className="text-lg font-semibold text-slate-900 dark:text-slate-100">
						{listaQuestoes.length} Questão(ões)
					</h3>
				</div>

				{/* Conteúdo do bloco */}
				<div className="space-y-4">
					{carregando ? (
						// Animação enquanto carrega
						<div className="flex justify-center items-center h-40">
							<div className="animate-spin rounded-full h-12 w-12 border-t-4 border-blue-600" />
						</div>
					) : erro ? (
						// Caso haja erro
						<div className="bg-red-50 dark:bg-red-950/20 border border-red-200 dark:border-red-900/50 text-red-700 dark:text-red-400 px-4 py-3 rounded text-center">
							<p>{erro}</p>
						</div>
					) : listaQuestoes.length === 0 ? (
						// Caso não haja questões
						<div className="text-center text-slate-500 dark:text-slate-400 py-10">
							<p>Nenhuma questão foi encontrada.</p>
							<p className="text-sm mt-2">Clique em "Adicionar Questão" para criar uma!</p>
						</div>
					) : (
						// Lista de questões paginadas
						questoesPaginadas.map((questao) => (
							<QuestaoCard
								key={questao.id}
								questao={questao}
								onExcluir={() => onExcluir(questao.id)}
								onEditarSucesso={onEditarSucesso}
								meusUpvotes={meusUpvotes}
							/>
						))
					)}
				</div>

				{/* Controles de Paginação */}
				{totalPages > 1 && (
					<div className="flex items-center justify-center gap-2 mt-6 pt-4 border-t border-slate-200 dark:border-slate-800">
						{/* Botão Anterior */}
						<button
							onClick={() => setPaginaAtual(prev => Math.max(prev - 1, 1))}
							disabled={paginaAtual === 1}
							className={`p-2 rounded-lg border transition-all duration-200 flex items-center justify-center ${paginaAtual === 1
								? 'bg-slate-50 dark:bg-slate-900 border-slate-200 dark:border-slate-800 text-slate-300 dark:text-slate-700 cursor-not-allowed'
								: 'bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 hover:border-blue-500 dark:hover:border-blue-500 text-slate-600 dark:text-slate-300 hover:text-blue-600 dark:hover:text-blue-400 hover:scale-105 active:scale-95 shadow-sm cursor-pointer'
								}`}
							title="Página Anterior"
						>
							<ChevronLeft size={18} />
						</button>

						{/* Números das Páginas */}
						<div className="flex items-center gap-1.5">
							{getPageNumbers().map((page, index) => {
								if (page === '...') {
									return (
										<span
											key={`dots-${index}`}
											className="px-3 py-1.5 text-slate-400 dark:text-slate-600 select-none font-medium"
										>
											...
										</span>
									);
								}
								return (
									<button
										key={page}
										onClick={() => setPaginaAtual(page)}
										className={`px-3.5 py-1.5 rounded-lg text-sm font-semibold transition-all duration-200 cursor-pointer ${paginaAtual === page
											? 'bg-blue-600 dark:bg-blue-600 text-white shadow-md shadow-blue-500/20'
											: 'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 hover:border-blue-500 dark:hover:border-blue-500 text-slate-600 dark:text-slate-300 hover:text-blue-600 dark:hover:text-blue-400 hover:scale-105 active:scale-95 shadow-sm'
											}`}
									>
										{page}
									</button>
								);
							})}
						</div>

						{/* Botão Próximo */}
						<button
							onClick={() => setPaginaAtual(prev => Math.min(prev + 1, totalPages))}
							disabled={paginaAtual === totalPages}
							className={`p-2 rounded-lg border transition-all duration-200 flex items-center justify-center ${paginaAtual === totalPages
								? 'bg-slate-50 dark:bg-slate-900 border-slate-200 dark:border-slate-800 text-slate-300 dark:text-slate-700 cursor-not-allowed'
								: 'bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 hover:border-blue-500 dark:hover:border-blue-500 text-slate-600 dark:text-slate-300 hover:text-blue-600 dark:hover:text-blue-400 hover:scale-105 active:scale-95 shadow-sm cursor-pointer'
								}`}
							title="Próxima Página"
						>
							<ChevronRight size={18} />
						</button>
					</div>
				)}
			</div>
		</div>
	);
}

export default QuestaoList;
