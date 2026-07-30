import { ShieldCheck, Award, GraduationCap } from 'lucide-react';

function Role({ usuario }) {
    if (!usuario) return null;

    // Se for Administrador -> Escudo Roxo
    if (usuario.administrador) {
        return (
            <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-purple-100 dark:bg-purple-950/60 text-purple-700 dark:text-purple-300 border border-purple-200 dark:border-purple-800">
                <ShieldCheck className="w-3.5 h-3.5 mr-1 text-purple-600 dark:text-purple-400" />
                Admin
            </span>
        );
    }

    // Se for Especialista -> Escudo Verde
    if (usuario.especialista) {
        return (
            <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-emerald-100 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800">
                <Award className="w-3.5 h-3.5 mr-1 text-emerald-600 dark:text-emerald-400" />
                Especialista
            </span>
        );
    }

    // Padrão (Usuário / Aluno) -> Ícone Cinza
    return (
        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-700">
            <GraduationCap className="w-3.5 h-3.5 mr-1 text-slate-500" />
            Usuário
        </span>
    );
}

export default Role;