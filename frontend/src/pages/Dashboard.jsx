import { useState } from 'react';
import QuestaoForm from './QuestaoForm';
import QuestaoList from '../components/questao/QuestaoList';
import { useQuestoes } from '../hooks/useQuestoes';
import PainelEstatisticas from '../components/dashboard/PainelEstatisticas';
import BotaoAdicionarQuestao from '../components/dashboard/BotaoAdicionarQuestao';

function Dashboard() {
	// Estado para controlar se o formulário está visível
	const [mostrarForm, setMostrarForm] = useState(false);
	const [termoBusca, setTermoBusca] = useState('');

	// Pega os métodos relacionados ao objeto questões pelo hook
	const { listaQuestoes, carregando, erro, removerDaLista, atualizarLista, pesquisar, editarNaLista, meusUpvotes } = useQuestoes();

	// Quando uma questão é salva com sucesso, fecha o formulário
	const handleSalvarQuestao = (questao) => {
		atualizarLista(questao);
		setMostrarForm(false); // Fecha o formulário
	};

	return (
		<div className="space-y-6">
			{/* Header da Dashboard */}
			<div className="flex items-center justify-between">
				<h2 className="text-3xl font-bold text-slate-900 dark:text-slate-100 transition-colors">Questões</h2>
			</div>

			<BotaoAdicionarQuestao
				onClick={() => setMostrarForm(!mostrarForm)}
				mostrarForm={mostrarForm}
			/>

			{/* Formulário (aparece/desaparece ao clicar) */}
			{mostrarForm && (
				<div className="bg-white dark:bg-slate-900 rounded-lg shadow-md p-6 border border-transparent dark:border-slate-800 transition-colors">
					<h3 className="text-xl font-bold mb-4 text-slate-900 dark:text-slate-100">Nova Questão</h3>
					<QuestaoForm SalvarSucesso={handleSalvarQuestao} />
				</div>
			)}

			<PainelEstatisticas listaQuestoes={listaQuestoes} />

			{/* Barra de Pesquisa */}
			<div className="bg-white dark:bg-slate-900 rounded-lg shadow-md p-4 border border-transparent dark:border-slate-800 transition-colors">
				<input
					type="text"
					placeholder="Pesquisar por enunciado, matéria, assunto ou fonte"
					className="w-full p-3 border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-transparent transition-colors"
					value={termoBusca}
					onChange={(e) => {
						setTermoBusca(e.target.value);
						pesquisar(e.target.value);
					}}
				/>
			</div>

			{/* Listagem de questões com busca e paginação separada */}
			<QuestaoList
				key={termoBusca}
				listaQuestoes={listaQuestoes}
				carregando={carregando}
				erro={erro}
				onExcluir={removerDaLista}
				onEditarSucesso={editarNaLista}
				meusUpvotes={meusUpvotes}
			/>
		</div>
	);
}

export default Dashboard;
