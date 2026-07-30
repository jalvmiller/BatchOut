import { Plus, X } from 'lucide-react';

function BotaoAdicionarQuestao({ onClick, mostrarForm }) {
    return (
        <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-50">
            <button
                onClick={onClick}
                className={`flex items-center gap-2.5 px-6 py-3.5 rounded-full font-bold text-white shadow-2xl transition-all duration-300 hover:scale-105 active:scale-95 border border-white/20 backdrop-blur-md cursor-pointer ${mostrarForm
                        ? 'bg-red-600 hover:bg-red-700 shadow-red-600/30'
                        : 'bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 shadow-blue-600/30'
                    }`}
            >
                {mostrarForm ? <X size={20} /> : <Plus size={20} />}
                <span>{mostrarForm ? 'Fechar Formulário' : 'Adicionar Questão'}</span>
            </button>
        </div>
    );
}

export default BotaoAdicionarQuestao;
