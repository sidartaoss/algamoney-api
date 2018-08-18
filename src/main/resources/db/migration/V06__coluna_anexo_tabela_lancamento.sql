-- MYSQL
-- Aula 22.34. Anexando Arquivo no Lancamento
-- 5. Vamos criar uma coluna nova na tabela LANCAMENTO.
ALTER TABLE lancamento ADD COLUMN anexo VARCHAR(200);
-- 5.1. Agora, vamos abrir a classe LancamentoService. Ver classe LancamentoService.java. 