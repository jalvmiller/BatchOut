import { useState } from 'react';
import Navbar from './Navbar';
import Sidebar from './Sidebar';

// Layout é o container principal
// Recebe children (conteúdo) que será exibido no espaço principal, Dashboard.jsx
function Layout({ children }) {
  // Estado para controlar se a sidebar está aberta em mobile
  const [sidebarOpen, setSidebarOpen] = useState(false);
  // Lembrar que o useState lida com o sidebarOpen como "atributo"
  // e que o setSidebarOpen fica atrelado a esse atributo como SETTER
  // False inicialmente

  // flex flex-col        -> flex em coluna, elementos filhos na vertical
  // h-screen             -> altura total da tela
  // bg-slate-50          -> cor de fundo da página
  // Navbar onMenuClick   -> renderiza a barra do topo, a OnMenuClick é uma função 
  // que invoca anonimamente o setter da sidebar como true
  // flex flex-1 overflow-hidden  -> flexbox em linha, flex-1 faz ocupar todo o
  // espaço da linha, overflow-hidden esconde qualquer coisa que saia da div  
  // flex-1 overflow-y-auto p-6   -> flex-1 ocupa todo o espaço,
  // o overflow-y-auto deixa rolar verticalmente quando o conteúdo for maior
  // que a tela

  return (
    <div className="relative flex flex-col h-screen bg-slate-100 dark:bg-slate-950 text-slate-900 dark:text-slate-100 transition-colors duration-300">
      {/* Fundo sutil com gradiente ambiente */}
      <div className="fixed inset-0 bg-gradient-to-br from-slate-100 via-blue-50/40 to-slate-200 dark:from-slate-950 dark:via-slate-900/80 dark:to-slate-950 pointer-events-none -z-10" />

      {/* Navbar no topo */}
      <Navbar onMenuClick={() => setSidebarOpen(true)} />

      {/* Container com sidebar + conteúdo principal */}
      <div className="relative z-10 flex flex-1 overflow-hidden">
        {/* Sidebar na lateral */}
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

        {/* Conteúdo principal (children) */}
        <main className="flex-1 overflow-y-auto p-6">
          {children}
        </main>
      </div>
    </div>
  );
}

export default Layout;
