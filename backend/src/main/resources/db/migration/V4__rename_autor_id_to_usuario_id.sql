-- Flyway Migration
-- Version: 4.0
-- Description: Rename autor_id to usuario_id in tables questoes and resolucoes
-- Author: João Müller
-- Date: 2026-07-25

-- Dropar antigas foreign keys despadronizadas
ALTER TABLE questoes DROP FOREIGN KEY fk_questoes_autor;
ALTER TABLE resolucoes DROP FOREIGN KEY fk_resolucoes_autor;

-- Renomear foreign keys
ALTER TABLE questoes RENAME COLUMN autor_id TO usuario_id;
ALTER TABLE resolucoes RENAME COLUMN autor_id TO usuario_id;

-- Recriar
ALTER TABLE questoes ADD CONSTRAINT fk_questoes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE SET NULL;
ALTER TABLE resolucoes ADD CONSTRAINT fk_resolucoes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE;
