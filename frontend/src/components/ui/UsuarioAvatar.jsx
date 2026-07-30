import { useState, useEffect } from 'react';
import { User } from "lucide-react";
import { getMediaUrl } from "@/utils/urlUtils";

// O avatar será composto por: Imagem (se existir) ou Letra Inicial do Nome
function UsuarioAvatar({ usuario, size = 'md' }) {
    const [imageError, setImageError] = useState(false);
    const avatarUrl = getMediaUrl(usuario?.avatar);

    useEffect(() => {
        setImageError(false);
    }, [usuario?.avatar]);

    // Mapear para n tamanhos (foco em reutilização; uso em card, painel, etc)
    const dimensao = {
        sm: 'w-8 h-8 text-xs',
        md: 'w-10 h-10 text-sm',
        lg: 'w-12 h-12 text-base',
    }[size] || 'w-10 h-10';

    //iconSize 
    const iconSize = {
        sm: 12,
        md: 16,
        lg: 20,
    }[size] || 16;

    // Obter a primeira letra, "U" (Usuário) se não existir
    const firstLetter = usuario?.nome?.charAt(0)?.toUpperCase() || 'U';

    return (
        <div className={`${dimensao} flex items-center justify-center flex-shrink-0 shadow-sm
        rounded-full overflow-hidden bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700`}>
            {avatarUrl && !imageError ? (
                <img
                    src={avatarUrl}
                    alt={usuario?.nome || usuario?.username || 'Avatar'}
                    className="w-full h-full object-cover"
                    onError={() => setImageError(true)}
                />
            ) : (
                <User size={iconSize} className="text-slate-400 dark:text-slate-500" />
            )}
        </div>
    );
}

export default UsuarioAvatar;