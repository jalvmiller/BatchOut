-- Flyway Migration
-- Version: 1
-- Description: Schema inicial do BatchOut — Sistema Corporativo de Gestão
--              de Despesas com Engine de Exportação Batch Assíncrona
-- Author: João Müller
-- Date: 2026-07-30

-- usuarios -> cria -> despesas -> são lidas via export_jobs

-- ==============================================================
-- 1. TABELA usuarios
--    Base de autenticação e autorização da aplicação.
--    Implementa UserDetails do Spring Security.
-- ==============================================================
CREATE TABLE usuarios (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(255) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    nome           VARCHAR(255),
    email          VARCHAR(255) UNIQUE,
    avatar         VARCHAR(500),
    pontos         INT          DEFAULT 0,
    -- especialista = gestor (pode aprovar/rejeitar despesas)
    especialista   BOOLEAN      DEFAULT FALSE,
    -- administrador = acesso total
    administrador  BOOLEAN      DEFAULT FALSE
);
-- Comportamento e atributos análogos ao do projeto PROBEND, por enquanto


-- ==============================================================
-- 2. TABELA despesas
--    Despesa corporativa registrada por um colaborador.
--    Ciclo de vida: PENDENTE -> APROVADA | REJEITADA
-- ==============================================================
CREATE TABLE despesas (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo          VARCHAR(255)   NOT NULL,
    descricao       TEXT,
    -- DECIMAL(10,2): suporta até 99.999.999,99 
    -- Enum armazenado como string para legibilidade e segurança de refactoring
    -- Data em que a despesa ocorreu de fato (informada pelo colaborador)
    -- Data automática de cadastro no sistema (gerenciada pelo @PrePersist)
    -- URL do comprovante no MinIO (nota fiscal, recibo, etc.)
    valor           DECIMAL(10, 2) NOT NULL,
    categoria       VARCHAR(50)    NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'PENDENTE',
    data_ocorrencia DATE           NOT NULL,
    data_criacao    DATETIME       NOT NULL,
    comprovante_url VARCHAR(500),
    -- FK do Colaborador que registrou a despesa 
    usuario_id      BIGINT         NOT NULL,
    -- FK do Gestor que aprovou ou rejeitou (NULL enquanto PENDENTE) e é OPCIONAL
    aprovador_id    BIGINT,
    -- Timestamp da aprovação do aprovador
    data_aprovacao  DATETIME,

    -- Justificativa obrigatória do gestor ao rejeitar
    motivo_rejeicao TEXT,

    CONSTRAINT fk_despesas_usuario
        FOREIGN KEY (usuario_id)   REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_despesas_aprovador
        FOREIGN KEY (aprovador_id) REFERENCES usuarios (id) ON DELETE SET NULL
);

-- ==============================================================
-- 3. TABELA export_jobs
--    Registra cada solicitação de exportação batch.
--    Padrão: Asynchronous Job Pattern
--    Ciclo de vida: ESPERANDO -> PROCESSANDO -> CONCLUIDO | FALHOU
-- ==============================================================
CREATE TABLE export_jobs (
    -- UUID como PK (string 36 chars), seguro para expor ao frontend como
    -- identificador de polling sem revelar sequência do banco
    id                VARCHAR(36)  PRIMARY KEY,
    status            VARCHAR(20)  NOT NULL DEFAULT 'ESPERANDO',
    tipo_exportacao   VARCHAR(10)  NOT NULL,

    -- Filtros do relatório serializados como JSON
    -- Exemplo: {"dataInicio":"2025-01-01","categoria":"TRANSPORTE","status":"APROVADA"}
    -- Armazenado como TEXT para flexibilidade, o Worker desserializa ao processar
    filtros           TEXT,

    -- Usuário que solicitou a exportação
    usuario_id        BIGINT       NOT NULL,
    data_solicitacao  DATETIME     NOT NULL,
    data_conclusao    DATETIME,

    -- URL pré-assinada do MinIO (expira 15 min após geração, segurança sem proxy)
    arquivo_url       VARCHAR(1000),

    -- Chave do objeto no bucket para regenerar a URL se expirar
    arquivo_key       VARCHAR(500),

    -- Quantidade de registros processados (métrica de performance)
    total_registros   BIGINT,

    -- Mensagem de erro para diagnóstico quando status = FALHOU
    mensagem_erro     TEXT,

    CONSTRAINT fk_export_jobs_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);
