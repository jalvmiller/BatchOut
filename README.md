<div align="center">

# BatchOut 📨

![Java](https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL%208.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
</div>

<div align="center">
O <b>BatchOut</b> é uma plataforma corporativa para gestão e exportação assíncrona de relatórios de despesas de grande volume.<br><br> <b><i>É um projeto pessoal que uso para fixar conhecimento relacionado a solução de problemas de performance. Esse problema da exportação de relatórios é algo que ocorre com frequência em empresas com sistemas mais antigos e em crescimento; portanto, achei um bom ponto de partida para um projeto de portfólio. E é algo passível de ser melhorado "infinitamente".</b></i> <br><br>Tenho a intenção de continuar implementando mais funções ligadas ao backend, já que é a área de Web que mais gosto de estudar. Eventualmente irei trazer uma interface web, mas o uso é feito via Swagger neste momento.</i><br><br>

[![Kanban & Backlog](https://img.shields.io/badge/GitHub_Projects-Kanban_%26_Backlog-238636?style=for-the-badge&logo=github&logoColor=white)](https://github.com/users/jalvmiller/projects/4)
</div>

### 📊 Benchmark via Swagger UI

| Métrica | Exportação Síncrona (Legado) | Exportação Assíncrona (BatchOut) | Otimização |
| :--- | :---: | :---: | :---: |
| **Tempo de Resposta (API)** | `4.800 ms` | **`15 ms`** | **99,7% mais rápido** |
| **Status HTTP Devolvido** | `200 OK` (após a espera) | **`202 Accepted`** (imediato) | **Liberativo** |
| **Consumo de Memória RAM** | Alto (carrega 100% dos dados) | **Baixo & Constante (Chunks de 500)** | **Previsível** |
| **Risco sob carga intensa** | `504 Timeout` / `OutOfMemory` | **Zero bloqueio HTTP** | **Resiliente** |

> *Métricas aferidas via cabeçalho HTTP `X-Response-Time-MS` instrumentado na aplicação.*



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