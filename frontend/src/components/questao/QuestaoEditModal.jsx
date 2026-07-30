import { useState } from "react";
import { createPortal } from "react-dom";
import { X } from "lucide-react";
import { questaoService } from "../../services/questaoService";

function QuestaoEditModal({ questao, isOpen, onClose, onSalvarSucesso }) {
    const [dados, setDados] = useState({
        enunciado: questao.enunciado || "",
        imagemUrl: questao.imagemUrl || "",
        materia: questao.materia || "",
        assunto: questao.assunto || "",
        dificuldade: questao.dificuldade !== undefined ? String(questao.dificuldade) : "0",
        fonte: questao.fonte || "",
        trechoCodigo: questao.trechoCodigo || "",
        linguagemCodigo: questao.linguagemCodigo || ""
    });
    const [salvando, setSalvando] = useState(false);
    const [melhorandoIA, setMelhorandoIA] = useState(false);
    const [enviandoImagem, setEnviandoImagem] = useState(false);

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        setEnviandoImagem(true);
        try {
            const data = await questaoService.uploadImagem(file);
            setDados(prev => ({ ...prev, imagemUrl: data.imageUrl }));
            alert("Imagem enviada com sucesso!");
        } catch (err) {
            console.error("Erro ao enviar imagem:", err);
            alert("Erro ao enviar imagem.");
        } finally {
            setEnviandoImagem(false);
        }
    };

    if (!isOpen) return null;

    const handleMelhorarEnunciadoIA = async () => {
        if (!dados.enunciado.trim()) {
            alert("Por favor, digite alguma coisa no enunciado para que a IA possa aprimorar.");
            return;
        }
        setMelhorandoIA(true);
        try {
            const dadosSugeridos = await questaoService.iaSugerir(
                "Melhore o enunciado desta questão para torná-lo mais claro, corrigindo erros gramaticais e aprimorando expressões matemáticas, mantendo o sentido original.",
                dados.enunciado
            );
            if (dadosSugeridos && dadosSugeridos.enunciado) {
                setDados(prev => ({
                    ...prev,
                    enunciado: dadosSugeridos.enunciado,
                    materia: dadosSugeridos.materia || prev.materia,
                    assunto: dadosSugeridos.assunto || prev.assunto,
                    dificuldade: dadosSugeridos.dificuldade !== undefined ? String(dadosSugeridos.dificuldade) : prev.dificuldade,
                    trechoCodigo: dadosSugeridos.trechoCodigo || prev.trechoCodigo,
                    linguagemCodigo: dadosSugeridos.linguagemCodigo || prev.linguagemCodigo
                }));
                alert("Enunciado aprimorado com sucesso pela IA!");
            }
        } catch (err) {
            console.error("Erro ao aprimorar com IA:", err);
            alert("Erro ao aprimorar enunciado com a IA. Certifique-se de que a API Key do Gemini está configurada.");
        } finally {
            setMelhorandoIA(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSalvando(true);
        try {
            const questaoAtualizada = await questaoService.atualizar(questao.id, {
                ...dados,
                dificuldade: parseInt(dados.dificuldade, 10)
            });
            onSalvarSucesso(questaoAtualizada);
            onClose();
        } catch (err) {
            console.error("Erro ao atualizar questão:", err);
            alert("Erro ao atualizar questão. Verifique se você é o autor ou se a conexão está ativa.");
        } finally {
            setSalvando(false);
        }
    };

    return createPortal(
        <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 flex items-center justify-center p-4 transition-colors duration-300">
            <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-xl max-w-2xl w-full max-h-[95vh] overflow-y-auto p-6 relative border border-transparent dark:border-slate-800 transition-colors duration-300 animate-in fade-in zoom-in-95 duration-200">
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 transition-colors cursor-pointer"
                    type="button"
                >
                    <X size={20} />
                </button>

                <h2 className="text-xl font-bold text-slate-900 dark:text-slate-100 mb-6 transition-colors">Editar Questão</h2>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <div className="flex justify-between items-center mb-1">
                            <label className="block text-sm font-semibold text-slate-700 dark:text-slate-300 transition-colors">
                                Enunciado
                            </label>
                            <button
                                type="button"
                                onClick={handleMelhorarEnunciadoIA}
                                className="text-xs font-bold text-violet-600 hover:text-violet-800 dark:text-violet-400 dark:hover:text-violet-300 transition-colors flex items-center gap-1 cursor-pointer disabled:opacity-50"
                                disabled={melhorandoIA}
                            >
                                {melhorandoIA ? "Aprimorando..." : "🪄 Melhorar com IA"}
                            </button>
                        </div>
                        <textarea
                            className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                            placeholder="Digite o enunciado"
                            value={dados.enunciado}
                            onChange={(e) => setDados({ ...dados, enunciado: e.target.value })}
                            rows="4"
                            required
                        />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div>
                            <label className="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1 transition-colors">
                                Matéria
                            </label>
                            <input
                                type="text"
                                className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                                placeholder="Ex: Java, Cálculo"
                                value={dados.materia}
                                onChange={(e) => setDados({ ...dados, materia: e.target.value })}
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1 transition-colors">
                                Assunto
                            </label>
                            <input
                                type="text"
                                className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                                placeholder="Ex: Orientação a Objetos"
                                value={dados.assunto}
                                onChange={(e) => setDados({ ...dados, assunto: e.target.value })}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1 transition-colors">
                                Dificuldade
                            </label>
                            <select
                                className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                                value={dados.dificuldade}
                                onChange={(e) => setDados({ ...dados, dificuldade: e.target.value })}
                            >
                                <option value="0">Fácil</option>
                                <option value="1">Médio</option>
                                <option value="2">Difícil</option>
                            </select>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1 transition-colors">
                                Fonte / Origem
                            </label>
                            <input
                                type="text"
                                className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                                placeholder="Ex: Autoral, Livro X, Aula Y"
                                value={dados.fonte}
                                onChange={(e) => setDados({ ...dados, fonte: e.target.value })}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1 transition-colors">
                                Upload de Imagem Ilustrativa
                            </label>
                            <input
                                type="file"
                                accept="image/*"
                                onChange={handleFileChange}
                                disabled={enviandoImagem}
                                className="w-full text-sm text-slate-500 dark:text-slate-400 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-xs file:font-bold file:bg-blue-50 file:text-blue-700 dark:file:bg-blue-950/40 dark:file:text-blue-400 hover:file:bg-blue-100 dark:hover:file:bg-blue-900/60 transition-all cursor-pointer"
                            />
                            {dados.imagemUrl && (
                                <p className="text-xs text-green-600 dark:text-green-400 mt-1 truncate">
                                    ✓ Imagem anexada
                                </p>
                            )}
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1 transition-colors">
                            Trecho de Código (Opcional)
                        </label>
                        <textarea
                            className="w-full p-2 font-mono text-sm border border-slate-300 dark:border-slate-800 rounded bg-slate-50 dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                            placeholder="Cole o código-fonte referente à questão aqui..."
                            value={dados.trechoCodigo}
                            onChange={(e) => setDados({ ...dados, trechoCodigo: e.target.value })}
                            rows="4"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1 transition-colors">
                            Linguagem do Código
                        </label>
                        <select
                            className="w-full p-2 border border-slate-300 dark:border-slate-800 rounded bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
                            value={dados.linguagemCodigo}
                            onChange={(e) => setDados({ ...dados, linguagemCodigo: e.target.value })}
                        >
                            <option value="">Texto Simples</option>
                            <option value="javascript">JavaScript</option>
                            <option value="python">Python</option>
                            <option value="java">Java</option>
                            <option value="c">C</option>
                            <option value="cpp">C++</option>
                            <option value="csharp">C#</option>
                            <option value="sql">SQL</option>
                        </select>
                    </div>

                    <div className="flex justify-end gap-3 pt-4 border-t border-slate-100 dark:border-slate-800 transition-colors">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 border border-slate-300 dark:border-slate-800 rounded text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 font-semibold transition-colors cursor-pointer"
                            disabled={salvando}
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded font-semibold transition-colors flex items-center justify-center min-w-[80px] cursor-pointer"
                            disabled={salvando}
                        >
                            {salvando ? "Salvando..." : "Salvar"}
                        </button>
                    </div>
                </form>
            </div>
        </div>,
        document.body
    );
}

export default QuestaoEditModal;
