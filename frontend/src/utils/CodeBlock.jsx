import React, { useEffect, useRef } from "react";
import Prism from "prismjs";

import "prismjs/themes/prism-tomorrow.css";
import "prismjs/components/prism-clike";
import "prismjs/components/prism-c";
import "prismjs/components/prism-cpp";
import "prismjs/components/prism-csharp";
import "prismjs/components/prism-java";
import "prismjs/components/prism-javascript";
import "prismjs/components/prism-python";
import "prismjs/components/prism-css";
import "prismjs/components/prism-sql";

// Mapeamento para padronizar as Strings
const MAPA_LINGUAGENS = {
    javascript: 'javascript',
    js: 'javascript',
    python: 'python',
    py: 'python',
    java: 'java',
    c: 'c',
    cpp: 'cpp',
    'c++': 'cpp',
    csharp: 'csharp',
    'c#': 'csharp',
    sql: 'sql'
};

export default function CodeBlock({ code, language }) {
    const codeRef = useRef(null);

    // Normaliza o nome da linguagem para o padrão
    const langMinusc = (language || '').toLowerCase().trim();
    const langPrism = MAPA_LINGUAGENS[langMinusc] || 'clike';
    // clike = genérica 

    // Aplicar sempre que o código ou a linguagem mudar
    useEffect(() => {
        if (codeRef.current) {
            Prism.highlightElement(codeRef.current);
        }
    }, [code, language]);

    if (!code) return null;

    return (
        <div className="overflow-hidden border border-slate-800 dark:border-slate-700 shadow-md my-4">
            {/* Cabeçalho do Bloco */}
            <div className="bg-slate-800 text-slate-300 px-4 py-2 text-xs font-mono flex justify-between items-center border-b border-slate-700">
                <span className="font-semibold uppercase tracking-wider">
                    Código em {language ? `(${language})` : ''}
                </span>
            </div>
            {/* Bloco de Código com Sintaxe Destacada */}
            <pre className="!bg-slate-950 !m-0 p-4 text-sm font-mono overflow-x-auto">
                <code ref={codeRef} className={`language-${langPrism}`}>
                    {code}
                </code>
            </pre>
        </div>
    );
}