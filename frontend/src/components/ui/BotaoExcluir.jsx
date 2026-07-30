import { questaoService } from "../../services/questaoService";

function BotaoExcluir({ idQuestao, aoExcluirSucesso }) {

    // Função que lida com o handle, ou apertar do botão
    const handleDeletar = async () => {
        if (!window.confirm("Tem certeza que quer deletar a questão?")) return;

        try {
            // Delegação da chamada para a service
            await questaoService.excluir(idQuestao);

            // Se o Spring respondeu com sucesso, avisamos o componente pai
            aoExcluirSucesso(idQuestao);
            alert("Questão removida do banco de dados!");
        } catch (err) {
            console.error("Erro ao deletar:", err);
            alert("Erro ao excluir: O servidor pode estar offline ou o ID não existe.");
        }
    };

    // Retorno em si do .jsx, é só o componente.. o tratamento do botão fica aqui por conveniência    
    return (
        <button
            onClick={handleDeletar}
            className="p-2 text-red-500 hover:bg-red-50 dark:hover:bg-red-950/20 rounded-full transition-colors group cursor-pointer"
            title="Excluir Questão"
        >
            Excluir Questão
        </button>
    );
}

export default BotaoExcluir;