import katex from 'katex';
import 'katex/dist/katex.min.css';

/**
 * Função utilitária para renderizar fórmulas matemáticas em LaTeX
 * usando a biblioteca KaTeX de forma.
 * 
 * Ela separa blocos de equações ($$ ... $$) e equações em linha ($ ... $)
 * e retorna elementos HTML seguros formatados.
 */

export const renderizarTextoMath = (texto) => {
    if (!texto) return "";

    // "partes" lida com Regex para encontrar padrões de Math inline (\$) ou em bloco (\$\$). 
    // O 'g' é para global, pega todos os caracteres.
    // A estrutura fica: 
    // (padrão1|padrão2), onde \$ significa o caractere literal $. 
    // [\s\S]*? significa qualquer caractere (incluindo quebras de linha)
    // Ou seja, ele procura por texto contidos dentro de um par de '$' ou '$$',
    // e captura tudo dentro desse par.
    const partes = texto.split(/(\$\$[\s\S]*?\$\$|\$[\s\S]*?\$)/g);
    return partes.map((parte, index) => {
        // Math Block
        // Se a parte começar e terminar com '$$', é um Math Block
        // A estrutura fica:
        // $$ formula $$ 
        // displayMode: true, define que é um Math Block
        // throwOnError: false, define que não vai dar erro se a fórmula estiver errada
        // key={index}, para garantir que cada div tenha uma chave única
        // dangerouslySetInnerHTML, para renderizar o HTML gerado pelo KaTeX
        if (parte.startsWith('$$') && parte.endsWith('$$')) {
            const formula = parte.slice(2, -2);
            const html = katex.renderToString(formula, {
                displayMode: true,
                throwOnError: false
            });

            return <div
                key={index} dangerouslySetInnerHTML={{
                    __html: html
                }}
                className="my-4 overflow-x-auto" />;
        }

        // Math Inline
        // O 'inline-block' garante que a fórmula não quebre linha e mantenha
        // o espaçamento correto em relação ao texto
        // displayMode: false, define que é uma fórmula inline
        // throwOnError: false, define que não vai dar erro se a fórmula estiver errada
        // key={index}, para garantir que cada span tenha uma chave única
        // dangerouslySetInnerHTML, para renderizar o HTML gerado pelo KaTeX
        if (parte.startsWith('$') && parte.endsWith('$')) {
            const formula = parte.slice(1, -1);
            const html = katex.renderToString(formula, {
                displayMode: false,
                throwOnError: false
            });

            return <span
                key={index} dangerouslySetInnerHTML={{
                    __html: html
                }}
                className="inline-block" />;
        }

        // Texto comum sem Math, só coloca dentro de uma tag span
        return <span key={index}>{parte}</span>;
    })
}