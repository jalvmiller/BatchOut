import { useState } from 'react';

function ResolucaoForm({ aoSubmeter, enviando }) {
    // Estados internos dos campos do formulário
    const [conteudo, setConteudo] = useState('');
    const [codigo, setCodigo] = useState('');
    const [linguagem, setLinguagem] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!conteudo.trim()) return;

        // Dispara o callback passando os dados preenchidos
        aoSubmeter({
            conteudo,
            trechoCodigo: codigo || null,
            linguagemCodigo: linguagem || null
        });

        // Limpa o formulário após a submissão
        setConteudo('');
        setCodigo('');
        setLinguagem('');
    };

    return (
        <form onSubmit={handleSubmit} className="bg-white dark:bg-slate-900 rounded-2xl shadow-lg border border-slate-100 dark:border-slate-800 p-6 space-y-4 transition-colors duration-300">
            <h4 className="font-bold text-slate-700 dark:text-slate-200 text-lg transition-colors">Registrar uma Resolução</h4>

            <textarea
                value={conteudo}
                onChange={(e) => setConteudo(e.target.value)}
                placeholder="Explique sua resolução aqui (suporta LaTeX como $E=mc^2$ ou $$\frac{a}{b}$$)..."
                className="w-full p-4 border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors min-h-[120px]"
                required
            />

            <textarea
                value={codigo}
                onChange={(e) => setCodigo(e.target.value)}
                placeholder="Trecho de código (opcional)"
                className="w-full p-4 border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-sm transition-colors min-h-[80px]"
            />

            {codigo && (
                <div className="flex flex-col gap-2">
                    <label className="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase transition-colors">Linguagem</label>
                    <select
                        value={linguagem}
                        onChange={(e) => setLinguagem(e.target.value)}
                        className="p-2 border border-slate-200 dark:border-slate-800 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors w-full sm:w-48 bg-white dark:bg-slate-950 text-slate-800 dark:text-slate-100"
                    >
                        <option value="">Sem linguagem</option>
                        <option value="java">Java</option>
                        <option value="python">Python</option>
                        <option value="javascript">JavaScript</option>
                        <option value="cpp">C++</option>
                        <option value="csharp">C#</option>
                        <option value="sql">SQL</option>
                    </select>
                </div>
            )}

            <button
                type="submit"
                disabled={enviando}
                className="bg-blue-600 hover:bg-blue-700 text-white font-bold px-6 py-2.5 rounded-xl transition cursor-pointer disabled:opacity-50"
            >
                {enviando ? 'Enviando...' : 'Enviar Resolução'}
            </button>
        </form>
    );
}

export default ResolucaoForm;
