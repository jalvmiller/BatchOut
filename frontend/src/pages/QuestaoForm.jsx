import { useState } from "react";
import { questaoService } from "../services/questaoService";

function QuestaoForm({ SalvarSucesso }) {

    const [novaQuestao, setNovaQuestao] = useState({
        enunciado: "",
        imagemUrl: "",
        materia: "",
        assunto: "",
        dificuldade: "0",
        fonte: "",
        trechoCodigo: "",
        linguagemCodigo: ""
    });

    const [enviandoImagem, setEnviandoImagem] = useState(false);
    const [promptIA, setPromptIA] = useState("");
    const [gerandoIA, setGerandoIA] = useState(false);

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        setEnviandoImagem(true);
        try {
            const data = await questaoService.uploadImagem(file);
            setNovaQuestao(prev => ({ ...prev, imagemUrl: data.imageUrl }));
            alert("Imagem enviada com sucesso!");
        } catch (err) {
            console.error("Erro ao enviar imagem:", err);
            alert("Erro ao enviar imagem.");
        } finally {
            setEnviandoImagem(false);
        }
    };

    const handleGerarEsbocoIA = async () => {
        if (!promptIA.trim()) {
            alert("Por favor, digite uma ideia para a IA no campo do Copiloto.");
            return;
        }
        setGerandoIA(true);
        try {
            const dadosSugeridos = await questaoService.iaSugerir(promptIA, novaQuestao.enunciado);
            setNovaQuestao({
                enunciado: dadosSugeridos.enunciado || "",
                materia: dadosSugeridos.materia || "",
                assunto: dadosSugeridos.assunto || "",
                dificuldade: String(dadosSugeridos.dificuldade ?? "0"),
                fonte: dadosSugeridos.fonte || "Gerado por Gemini",
                trechoCodigo: dadosSugeridos.trechoCodigo || "",
                linguagemCodigo: dadosSugeridos.linguagemCodigo || ""
            });
            alert("Esboço gerado com sucesso! Você pode revisar os campos abaixo.");
        } catch (err) {
            console.error("Erro ao gerar esboço com IA:", err);
            alert("Erro ao obter sugestão da IA. Certifique-se de que a API Key do Gemini está configurada.");
        } finally {
            setGerandoIA(false);
        }
    };

    const handleCriarTotalIA = async () => {
        if (!promptIA.trim()) {
            alert("Por favor, digite uma ideia para a IA no campo do Copiloto.");
            return;
        }
        setGerandoIA(true);
        try {
            const questaoSalva = await questaoService.iaCriarTotal(promptIA);
            SalvarSucesso(questaoSalva);
            setPromptIA("");
            alert("Questão gerada e cadastrada no banco de dados com sucesso!");
        } catch (err) {
            console.error("Erro ao publicar com IA:", err);
            alert("Erro ao publicar questão direta com a IA. Certifique-se de que a API Key do Gemini está configurada.");
        } finally {
            setGerandoIA(false);
        }
    };

    const handleSalvar = async (e) => {
        e.preventDefault();

        try {
            const dadosSalvos = await questaoService.salvar(novaQuestao);
            // Avisar o componente pai (App.js) que a questão foi salvo, enviar o novo objeto
            SalvarSucesso(dadosSalvos);
            // Limpar formulário para o próximo POST
            setNovaQuestao({
                enunciado: "",
                imagemUrl: "",
                materia: "",
                assunto: "",
                dificuldade: "0",
                fonte: "",
                trechoCodigo: "",
                linguagemCodigo: ""
            });

            alert("Questão salva com sucesso!");
        } catch (err) {
            console.error("Erro ao salvar:", err);
            alert("Erro ao conectar com o Spring. Verifique o console.");
        }
    };

    return (
        <form onSubmit={handleSalvar} className="w-full">

            {/* Bloco Auxiliar de IA */}
            <div className="mb-6 p-4 bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 rounded-xl space-y-3 transition-colors">
                <label className="block text-sm font-bold text-slate-700 dark:text-slate-200 flex items-center gap-1 transition-colors">
                    🪄 Copiloto de Criação IA (Gemini)
                </label>
                <p className="text-xs text-slate-500 dark:text-slate-400 transition-colors">
                    Digite sua ideia (ex: "Crie uma questão de Cálculo 1 sobre limites fundamentais") e deixe a IA preencher o formulário para você.
                </p>
                <textarea
                    className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded text-sm bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                    placeholder="Descreva a questão que deseja criar..."
                    value={promptIA}
                    onChange={(e) => setPromptIA(e.target.value)}
                    rows="2"
                    disabled={gerandoIA}
                />
                <div className="flex gap-2">
                    <button
                        type="button"
                        onClick={handleGerarEsbocoIA}
                        className="flex-1 bg-violet-600 hover:bg-violet-700 text-white text-xs font-bold py-2 px-3 rounded transition disabled:opacity-50 cursor-pointer"
                        disabled={gerandoIA}
                    >
                        {gerandoIA ? "Gerando..." : "💡 Preencher Esboço"}
                    </button>
                    <button
                        type="button"
                        onClick={handleCriarTotalIA}
                        className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold py-2 px-3 rounded transition disabled:opacity-50 cursor-pointer"
                        disabled={gerandoIA}
                    >
                        {gerandoIA ? "Publicando..." : "✨ Publicar Direto"}
                    </button>
                </div>
            </div>

            <textarea
                className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded mb-3 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                placeholder="Digite o enunciado:"
                value={novaQuestao.enunciado}
                onChange={(e) => setNovaQuestao({ ...novaQuestao, enunciado: e.target.value })}
                required
            />

            <div className="flex gap-4 mb-4">
                <input
                    className="flex-1 p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                    placeholder="Matéria (ex: Java)"
                    value={novaQuestao.materia}
                    onChange={(e) => setNovaQuestao({ ...novaQuestao, materia: e.target.value })}
                    required
                />

                <input
                    className="flex-1 p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                    placeholder="Assunto"
                    value={novaQuestao.assunto}
                    onChange={(e) => setNovaQuestao({ ...novaQuestao, assunto: e.target.value })}
                />

                <select
                    className="p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                    value={novaQuestao.dificuldade}
                    onChange={(e) => setNovaQuestao({ ...novaQuestao, dificuldade: e.target.value })}
                >
                    <option value="0">Fácil</option>
                    <option value="1">Médio</option>
                    <option value="2">Difícil</option>
                </select>
            </div>

            <input
                className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded mb-4 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                placeholder="Fonte (ex: Livro X, Aula Y)"
                value={novaQuestao.fonte}
                onChange={(e) => setNovaQuestao({ ...novaQuestao, fonte: e.target.value })}
            />

            <textarea
                className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded mb-3 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                placeholder="Trecho de código (opcional)"
                value={novaQuestao.trechoCodigo}
                onChange={(e) => setNovaQuestao({ ...novaQuestao, trechoCodigo: e.target.value })}
                rows="4"
            />

            {/* Campo de Upload de Imagem */}
            <div className="mb-4">
                <label className="block text-sm font-bold text-slate-700 dark:text-slate-200 mb-1">
                    Imagem da Questão (Opcional)
                </label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    disabled={enviandoImagem}
                    className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
                />
                {enviandoImagem && <p className="text-xs text-blue-500 mt-1">Enviando imagem...</p>}

                {novaQuestao.imagemUrl && (
                    <div className="mt-2 relative inline-block">
                        <img
                            src={novaQuestao.imagemUrl.startsWith("http") ? novaQuestao.imagemUrl : `http://localhost:8080${novaQuestao.imagemUrl}`}
                            alt="Preview da imagem"
                            className="max-h-40 rounded border border-slate-200 dark:border-slate-800"
                        />
                        <button
                            type="button"
                            onClick={() => setNovaQuestao(prev => ({ ...prev, imagemUrl: "" }))}
                            className="absolute -top-2 -right-2 bg-red-600 hover:bg-red-700 text-white rounded-full p-1 text-xs w-6 h-6 flex items-center justify-center cursor-pointer font-bold"
                            title="Remover imagem"
                        >
                            ✕
                        </button>
                    </div>
                )}
            </div>


            <select
                className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded mb-4 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                value={novaQuestao.linguagemCodigo}
                onChange={(e) => setNovaQuestao({ ...novaQuestao, linguagemCodigo: e.target.value })}
            >
                <option value="">Sem linguagem</option>
                <option value="java">Java</option>
                <option value="python">Python</option>
                <option value="javascript">JavaScript</option>
                <option value="cpp">C++</option>
                <option value="csharp">C#</option>
                <option value="sql">SQL</option>
            </select>

            <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded font-bold hover:bg-blue-700 w-full cursor-pointer">
                Adicionar Questão
            </button>
        </form>
    );
};

export default QuestaoForm;