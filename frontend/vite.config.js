import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { fileURLToPath, URL } from 'node:url' // utilitário para path do node

import tailwindcss from '@tailwindcss/vite' // importar o plugin do tailwind

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  },
  resolve: {
    alias: {
      // fileURLToPath para que o vite saiba onde está a pasta src
      // import.meta.url é uma variável global que contém o path absoluto
      // do módulo atual. O "./src" é o caminho relativo da raiz do projeto
      // e o "./src" indica a pasta que será aliasada. Com isso, dá pra
      // importar arquivos usando @/ nome_do_arquivo em vez de ../nome_do_arquivo
      // O Vite entende isso automaticamente
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
