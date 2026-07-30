import { useRef, useState } from 'react';
import api from '../services/api';
import { useAuth } from './useAuth';

/**
 * Hook para gerenciar o upload de foto de perfil (avatar).
 * isolar a manipulação de referências de input, FormData e chamadas à API. 
 * @param {Function} onSuccess callback quando o upload for concluído com sucesso.
 */
export function useUser(onSuccess) {
    const fileInputRef = useRef(null);
    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState(null);
    const { setUser } = useAuth();

    // Função para acionar a janela nativa de escolha de arquivos
    const abrirSeletorDeArquivo = () => {
        fileInputRef.current?.click();
    };

    // Handler disparado ao selecionar um arquivo
    const handleFileChange = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;

        setErro(null);
        setCarregando(true);

        const formData = new FormData();
        formData.append('file', file);

        try {
            // convenção "me", padrão de mercado para APIs REST para se referir ao usuário logado
            // Backend não aceita ID vindo da URL, então usa "me" como placeholder
            // e o frontend não precisa concatenar o ID do usuário na URL toda vez
            const response = await api.post('usuarios/me/avatar', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            // Atualiza o estado global do usuário no AuthContext
            if (setUser) {
                setUser(response.data);
            }

            if (onSuccess) {
                onSuccess(response.data);
            }
        } catch (err) {
            console.error('Erro ao fazer upload do avatar:', err);
            setErro(err.response?.data?.message || 'Erro ao atualizar a foto de perfil.');
        } finally {
            setCarregando(false);
            // Limpa o valor do input para permitir selecionar a mesma imagem novamente se necessário
            if (fileInputRef.current) {
                fileInputRef.current.value = '';
            }
        }
    };

    return {
        fileInputRef,
        abrirSeletorDeArquivo,
        handleFileChange,
        carregando,
        erro
    };
}
