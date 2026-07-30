-- Flyway
-- Version: 1.0
-- Description: Primeiro schema do probend com todas as tabelas necessárias,
-- histórico mantido no próprio banco via tabela flyway_schema_history
-- Author: João Müller
-- Date: 2026-07-18

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(255),
    pontos INT DEFAULT 0,
    especialista BOOLEAN DEFAULT FALSE,
    administrador BOOLEAN DEFAULT FALSE
);

CREATE TABLE questoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enunciado TEXT NOT NULL,
    imagem_url VARCHAR(500),
    materia VARCHAR(255) NOT NULL,
    assunto VARCHAR(255),
    dificuldade INT NOT NULL,
    fonte VARCHAR(255),
    autor_id BIGINT,
    upvotes INT DEFAULT 0,
    trecho_codigo TEXT,
    linguagem_codigo VARCHAR(50),
    solucionada BOOLEAN DEFAULT FALSE,
    data_insercao DATETIME,
    CONSTRAINT fk_questoes_autor FOREIGN KEY (autor_id) REFERENCES usuarios (id) ON DELETE SET NULL
);

CREATE TABLE resolucoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conteudo TEXT NOT NULL,
    trecho_codigo TEXT,
    linguagem_codigo VARCHAR(50),
    upvotes INT DEFAULT 0,
    verificado_por_especialista BOOLEAN DEFAULT FALSE,
    questao_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    CONSTRAINT fk_resolucoes_questao FOREIGN KEY (questao_id) REFERENCES questoes (id) ON DELETE CASCADE,
    CONSTRAINT fk_resolucoes_autor FOREIGN KEY (autor_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE upvotes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    questao_id BIGINT,
    resolucao_id BIGINT,
    CONSTRAINT fk_upvotes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_upvotes_questao FOREIGN KEY (questao_id) REFERENCES questoes (id) ON DELETE CASCADE,
    CONSTRAINT fk_upvotes_resolucao FOREIGN KEY (resolucao_id) REFERENCES resolucoes (id) ON DELETE CASCADE,
    CONSTRAINT uq_upvote_usuario_questao UNIQUE (usuario_id, questao_id),
    CONSTRAINT uq_upvote_usuario_resolucao UNIQUE (usuario_id, resolucao_id)
);
