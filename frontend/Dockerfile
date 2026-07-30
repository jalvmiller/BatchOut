# build da aplicação React + Vite
FROM node:20-alpine AS build

WORKDIR /app

# copiar arquivos de dependências
COPY package*.json ./

# instalar dependências
RUN npm ci

# copiar o restante do código-fonte
COPY . .

# executar o build de produção (gera pasta /dist)
RUN npm run build

# servidor Nginx para servir arquivos estáticos
FROM nginx:alpine

# copiar configuração personalizada do Nginx para suporte a SPA (React Router)
COPY --from=build /app/nginx.conf /etc/nginx/conf.d/default.conf

# copiar os arquivos compilados da etapa anterior para a pasta do Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# expor a porta 80 (usada pelo Nginx para servir os arquivos estáticos)
EXPOSE 80

# comando para iniciar o Nginx (mantém o container rodando)
CMD ["nginx", "-g", "daemon off;"]
