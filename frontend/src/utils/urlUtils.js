/**
 * Converte um caminho relativo de mídia (ex: /midia/imagens/xyz.png)
 * em uma URL absoluta apontando para a API do backend.
 * 
 * @param {string} path - Caminho relativo ou URL da imagem
 * @returns {string|null} URL formatada para exibição em tags <img>
 */
export function getMediaUrl(path) {
    if (!path) return null;

    // Se já for uma URL completa (http:// ou https://)
    if (path.startsWith('http://') || path.startsWith('https://')) {
        return path;
    }

    const apiBase = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

    // Se path começar com /api, remove o /api para evitar duplicação
    const cleanPath = path.startsWith('/api') ? path.replace('/api', '') : path;

    // Remove /api do final da apiBase para obter o host base (ex: http://localhost:8080)
    const origin = apiBase.replace(/\/api\/?$/, '');

    return `${origin}/api${cleanPath.startsWith('/') ? '' : '/'}${cleanPath}`;
}
