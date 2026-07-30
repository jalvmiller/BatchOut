import { useState, useEffect } from "react";
import { questaoService } from "../services/questaoService";

export function useQuestoes() {

	//       getter             setter          lista vazia
	const [listaQuestoes, setListaQuestoes] = useState([]);
	const [carregando, setCarregando] = useState(true);
	const [erro, setErro] = useState(null); // Estado p/ erros
	const [meusUpvotes, setMeusUpvotes] = useState([]); // Estado para as questões curtidas

	useEffect(() => {
		const carregarDadosBanco = async () => {
			try {
				setCarregando(true);
				setErro(null); // Resetar antes de buscar

				const dados = await questaoService.listarTodas();
				setListaQuestoes(dados);

				const token = localStorage.getItem('token');
				if (token) {
					const upvotes = await questaoService.getMeusUpvotes();
					setMeusUpvotes(upvotes);
				}
			} catch (err) {
				console.error("Erro ao conectar no Spring", err);
				setErro("O servidor não está ligado");
			} finally {
				setCarregando(false);
				// O bloco contido no carregando vai aparecer depois do return
				// com o setCarregando(false)
			}
		};

		carregarDadosBanco();

	}, []); // array vazio no final, ele indica que esse trecho deve ser executado "onLoad"


	const removerDaLista = (id) => {
		// Cria uma nova lista SEM a questão que tem o ID deletado
		const listaAtualizada = listaQuestoes.filter(q => q.id !== id);
		setListaQuestoes(listaAtualizada);
	};

	// Chamada pelo QuestaoForm quando um POST for bem-sucedido
	const atualizarLista = (novaQuestao) => {
		// Nova questão no início da lista para o usuário ver na hora
		setListaQuestoes([novaQuestao, ...listaQuestoes]);
	};

	const pesquisar = async (termo) => {
		try {
			setCarregando(true);
			const dados = await questaoService.busca(termo);
			setListaQuestoes(dados);
		} catch (error) {
			console.error("Erro na busca", error);
		} finally {
			setCarregando(false);
		}
	};

	const editarNaLista = (questaoAtualizada) => {
		setListaQuestoes(prev => prev.map(q => q.id === questaoAtualizada.id ? questaoAtualizada : q));
		// O map faz uma cópia de cada elemento e os une em um novo array.
		// Se o id não bater, ele retorna o original, senão, retorna o atualizado.
		// prev => prev.map(...)
		// prev é o estado anterior, a seta indica função anônima que usa de prev como argumento
	};

	// Retornamos tudo que a tela precisa para funcionar
	return { listaQuestoes, carregando, erro, removerDaLista, atualizarLista, pesquisar, editarNaLista, meusUpvotes, setMeusUpvotes };
}