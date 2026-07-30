import { HelpCircle, CheckCircle2, Clock } from 'lucide-react';

function PainelEstatisticas({ listaQuestoes = [] }) {
    const total = listaQuestoes.length;
    const solucionadas = listaQuestoes.filter(q => q.solucionada).length;
    const pendentes = listaQuestoes.filter(q => !q.solucionada).length;

    return (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            {/* Total de Questões */}
            <div className="bg-white dark:bg-slate-900 p-5 rounded shadow-sm border border-slate-200/80 dark:border-slate-800 flex items-center gap-4 transition-all duration-300 hover:shadow-md">
                <div className="p-3.5 bg-blue-50 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400 rounded-xl border border-blue-100 dark:border-blue-900/40">
                    <HelpCircle size={24} />
                </div>
                <div>
                    <p className="text-xs font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider">
                        Total de Questões
                    </p>
                    <h3 className="text-2xl font-black text-slate-800 dark:text-slate-100 mt-0.5">
                        {total}
                    </h3>
                </div>
            </div>

            {/* Pendentes */}
            <div className="bg-white dark:bg-slate-900 p-5 rounded shadow-sm border border-slate-200/80 dark:border-slate-800 flex items-center gap-4 transition-all duration-300 hover:shadow-md">
                <div className="p-3.5 bg-amber-50 dark:bg-amber-950/40 text-amber-600 dark:text-amber-400 rounded-xl border border-amber-100 dark:border-amber-900/40">
                    <Clock size={24} />
                </div>
                <div>
                    <p className="text-xs font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider">
                        Pendentes
                    </p>
                    <h3 className="text-2xl font-black text-slate-800 dark:text-slate-100 mt-0.5">
                        {pendentes}
                    </h3>
                </div>
            </div>

            {/* Solucionadas */}
            <div className="bg-white dark:bg-slate-900 p-5 rounded shadow-sm border border-slate-200/80 dark:border-slate-800 flex items-center gap-4 transition-all duration-300 hover:shadow-md">
                <div className="p-3.5 bg-emerald-50 dark:bg-emerald-950/40 text-emerald-600 dark:text-emerald-400 rounded-xl border border-emerald-100 dark:border-emerald-900/40">
                    <CheckCircle2 size={24} />
                </div>
                <div>
                    <p className="text-xs font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider">
                        Solucionadas
                    </p>
                    <h3 className="text-2xl font-black text-slate-800 dark:text-slate-100 mt-0.5">
                        {solucionadas}
                    </h3>
                </div>
            </div>
        </div>
    );
}

export default PainelEstatisticas;