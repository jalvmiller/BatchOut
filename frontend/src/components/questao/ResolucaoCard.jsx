import { useState, useEffect } from 'react';
import { ThumbsUp } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { questaoService } from '../../services/questaoService';
import { renderizarTextoMath } from '../../utils/mathRenderer';
import CodeBlock from '../../utils/CodeBlock';
import Role from '../ui/Role';
import UsuarioAvatar from '../ui/UsuarioAvatar';
import DataFormatada from '../ui/DataFormatada';

function ResolucaoCard({ resolucao, meusUpvotes }) {
    const { user } = useAuth();
    const [upvotesCount, setUpvotesCount] = useState(resolucao.upvotes || 0);
    const [isUpvoted, setIsUpvoted] = useState(false);

    useEffect(() => {
        if (meusUpvotes && meusUpvotes.includes(resolucao.id)) {
            setIsUpvoted(true);
        } else {
            setIsUpvoted(false);
        }
    }, [meusUpvotes, resolucao.id]);

    const handleUpvote = async (e) => {
        e.preventDefault();
        e.stopPropagation();

        if (!user) {
            alert("Você precisa estar logado para dar upvote!");
            return;
        }

        try {
            const resultado = await questaoService.upvoteResolucao(resolucao.id);
            setUpvotesCount(resultado.upvotes);
            setIsUpvoted(resultado.upvoted);
        } catch (err) {
            console.error("Erro ao dar upvote na resolução:", err);
        }
    };

    return (
        // Janela do Card de Resolução
        <div className='bg-white dark:bg-slate-900 rounded-2xl shadow-lg border border-slate-100 dark:border-slate-800 p-6 space-y-4 transition hover:shadow-xl transition-colors duration-300'>
            <div className='flex justify-between items-center border-b border-slate-50 dark:border-slate-800 pb-2'>
                {/* Informações sobre quem postou a resolução */}
                <div className='flex items-center space-x-3'>
                    <UsuarioAvatar usuario={resolucao.autor} size="md" />

                    <div className="flex flex-col">
                        <div className="flex items-center gap-2">
                            <span className='text-sm font-semibold text-slate-600 dark:text-slate-400'>
                                Respondido por: <span className='text-slate-800 dark:text-slate-200'>{resolucao.autor?.nome || resolucao.autor?.username}</span>
                            </span>
                            <Role usuario={resolucao.autor} />
                        </div>
                        {resolucao.dataCriacao && (
                            <DataFormatada data={resolucao.dataCriacao} />
                        )}
                    </div>
                </div>

                {/* Botão de Upvote */}
                <button
                    onClick={handleUpvote}
                    className={`flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-bold transition-all duration-200 cursor-pointer border ${isUpvoted
                        ? 'bg-blue-50 dark:bg-blue-950/30 text-blue-600 dark:text-blue-400 border-blue-200 dark:border-blue-900/50 hover:bg-blue-100'
                        : 'bg-slate-50 dark:bg-slate-800 text-slate-600 dark:text-slate-400 border-slate-200 dark:border-slate-700 hover:bg-slate-100 hover:text-slate-800'
                        }`}
                    title={isUpvoted ? "Remover Upvote" : "Dar Upvote"}
                >
                    <ThumbsUp size={20} className={isUpvoted ? "fill-current text-blue-600 dark:text-blue-400" : ""} />
                    <span className="text-sm font-bold">{upvotesCount}</span>
                </button>
            </div>

            {/* Renderização do conteúdo da resolução
            * leading-relaxed aumenta o espaçamento entre linhas para melhor leitura
            * o leading-relaxed seta a altura da linha com 1.625 ou 162.5% do tamanho da fonte
            * desse modo o texto fica menos "apertado"
            * o whitespace-pre-wrap mantém a formatação do texto (incluindo quebras de linha)
            * sendo que o conteúdo vem do banco de dados com quebras de linha \n         
            */}
            <div className='text-slate-800 dark:text-slate-100 leading-relaxed whitespace-pre-wrap transition-colors duration-300'>
                {renderizarTextoMath(resolucao.conteudo)}
            </div>

            {/* Renderização do trecho de código (se houver) */}
            {resolucao.trechoCodigo && (
                <CodeBlock
                    code={resolucao.trechoCodigo}
                    language={resolucao.linguagemCodigo}
                />
            )}
        </div>
    );
}

export default ResolucaoCard;