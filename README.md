<div align="center">

# BatchOut 📨

### 📌 Sobre o Projeto + Stack Completa
<div align="center">
O <b>BatchOut</b> é uma plataforma corporativa para gestão e exportação assíncrona de relatórios de despesas de grande volume.
<br><br>
É um projeto pessoal que uso para fixar conhecimento relacionado a solução de problemas de performance em sistemas de grande porte. Por isso, tenho a intenção de continuar melhorando este projeto, e eventualmente trazer uma interface web quando implementar mais funcionalidades<br><br>
</div>

![Java](https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL%208.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
</div>

### **♠️ Backend (Tecnologias Estruturais) & APIs**
 ![Java](https://img.shields.io/badge/-Java%2021-007396?style=flat-square&logo=openjdk&logoColor=white) **Java 21 & Spring Boot 3** — Núcleo e lógica da aplicação REST API\
 ![Spring Security](https://img.shields.io/badge/-Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white) **Spring Security & JWT** — Autenticação *stateless* e controle de acesso baseado em papéis\
 ![Redis](https://img.shields.io/badge/-Redis-DC382D?style=flat-square&logo=redis&logoColor=white) **Redis** — Gerenciamento de *blacklist* de tokens JWT e cache\
 ![Hibernate](https://img.shields.io/badge/-Spring%20Data%20JPA-59666C?style=flat-square&logo=hibernate&logoColor=white) **Spring Data JPA & Hibernate** — Mapeamento objeto-relacional e paginação em lote\
 ![MySQL](https://img.shields.io/badge/-MySQL%208.0-4479A1?style=flat-square&logo=mysql&logoColor=white) **MySQL 8.0** — Banco de dados relacional principal\
 ![Flyway](https://img.shields.io/badge/-Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white) **Flyway** — Migração e versionamento automatizado do schema do banco\
 ![Swagger](https://img.shields.io/badge/-OpenAPI%20%2F%20Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black) **Springdoc OpenAPI** — Documentação interativa e testável das rotas

### **🔧 Mensageria, Storage & Processamento Assíncrono**
 ![RabbitMQ](https://img.shields.io/badge/-RabbitMQ-FF6600?style=flat-square&logo=rabbitmq&logoColor=white) **RabbitMQ & Spring AMQP** — Fila de mensagens para desacoplamento de exportações pesadas\
 ![MinIO](https://img.shields.io/badge/-MinIO%20%2F%20S3-C42C23?style=flat-square&logo=minio&logoColor=white) **MinIO SDK** — Armazenamento de objetos compatível com AWS S3 com geração de Pre-signed URLs

### **📚 Frontend & Interface**
 ![React](https://img.shields.io/badge/-React%2018-61DAFB?style=flat-square&logo=react&logoColor=black) **React 18 & Vite** — Painel administrativo para criação de despesas e solicitação de relatórios\
 ![Axios](https://img.shields.io/badge/-Axios-5A29E4?style=flat-square&logo=axios&logoColor=white) **Axios** — Cliente HTTP para comunicação com o backend\
 ![CSS](https://img.shields.io/badge/-CSS3%20Customizado-1572B6?style=flat-square&logo=css3&logoColor=white) **CSS Customizado** — Layout corporativo e responsivo

---

### 🚀 Painéis e Serviços Locais

Após subir o ambiente via Docker Compose, acesse os serviços nos links abaixo:

| Serviço | Porta | URL de Acesso | Credenciais Padrão |
| :--- | :---: | :--- | :--- |
| **Frontend App** | `5173` | [localhost:5173](http://localhost:5173) | *N/A* |
| **Backend API & Swagger UI** | `8080` | [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) | *Acesso público* |
| **MinIO Console** | `9001` | [localhost:9001](http://localhost:9001) | `minioadmin` / `minioadminpassword` |
| **MinIO API (S3 Endpoint)** | `9000` | [localhost:9000](http://localhost:9000) | *Definidas via SDK / .env* |
| **RabbitMQ Management** | `15672` | [localhost:15672](http://localhost:15672) | `guest` / `guest` |
| **Redis** | `6379` | `localhost:6379` | *Definidas via container* |

---

### 🔧 Passos para Executar (pré-requisitos: Docker e Docker Compose)

```bash
# 1. Clone o repositório
git clone [https://github.com/joaomu/batchout.git](https://github.com/joaomu/batchout.git)

# 2. Acesse o diretório do projeto
cd batchout

# 3. Suba o ecossistema completo de containers
docker compose up -d