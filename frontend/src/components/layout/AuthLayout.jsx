import React from 'react';

export function AuthLayout({ children }) {
  return (
    <div className="relative min-h-screen flex items-center justify-center bg-slate-100 dark:bg-slate-950 p-4 transition-colors duration-300">
      {/* Máscara de textura de pontos via CSS sutil */}
      <div className="absolute inset-0 bg-[radial-gradient(#e2e8f0_1px,transparent_1px)] dark:bg-[radial-gradient(#1e293b_1px,transparent_1px)] [background-size:24px_24px] pointer-events-none" />

      {/* Container principal */}
      <div className="relative z-10 w-full max-w-md p-8 bg-white dark:bg-slate-900 shadow-xl border border-slate-200 dark:border-slate-800 transition-colors duration-300">

        {/* Faixa gradiente superior */}
        <div className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-blue-500 via-indigo-500 to-cyan-400" />
        {/* posição da logo */}
        <div className="flex items-center justify-center gap-2 mb-6">
          {/* fundo da logo */}
          <div className="w-full h-12 bg-blue-600 flex items-center p-4 justify-center shadow-md shadow-blue-500/30">
            {/* texto, ou logo */}
            <span className="text-2xl font-extrabold tracking-wider bg-gradient-to-r from-white via-blue-100 to-cyan-200 bg-clip-text text-transparent">
              PROBEND
            </span>
          </div>
        </div>
        {/* form */}
        {children}
      </div>
    </div>
  );
}

export default AuthLayout;
