import { Clock } from 'lucide-react';

function DataFormatada({ data }) {
    if (!data) return null;

    const dataFormatada = new Date(data).toLocaleDateString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    return (
        <span className='text-xs text-slate-400 dark:text-slate-500 flex items-center gap-1 mt-0.5'>
            <Clock size={12} />
            {dataFormatada}
        </span>
    );
}

export default DataFormatada;
