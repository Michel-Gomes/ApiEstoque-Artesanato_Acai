-- =============================================================
-- BANCO DE DADOS: estoque_db
-- API: estoqueApi
-- AUTOR: Michel Gomes
-- DATA: CURRENT_DATE
-- DESCRIÇÃO: Estrutura inicial do banco de dados da API de Estoque
-- =============================================================

-- ===========================================================
--  CRIAÇÃO DO BANCO
-- ===========================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Drop objetos (útil para desenvolvimento)
DROP TABLE IF EXISTS movimentacao_loja CASCADE;
DROP TABLE IF EXISTS movimentacao_fabrica CASCADE;
DROP TABLE IF EXISTS estoque_loja CASCADE;
DROP TABLE IF EXISTS estoque_fabrica CASCADE;
DROP TABLE IF EXISTS produtos CASCADE;

-- Verificar tabelas

SELECT * FROM estoque_loja;
SELECT * FROM estoque_fabrica;
SELECT * FROM produtos;
SELECT * FROM lote_fabricacao;
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'categoria_enum') THEN
    DROP TYPE categoria_enum;
  END IF;
  IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipo_movimentacao_enum') THEN
    DROP TYPE tipo_movimentacao_enum;
  END IF;
  IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'unidade_medida_enum') THEN
    DROP TYPE unidade_medida_enum;
  END IF;
END
$$;

CREATE TYPE categoria_enum AS ENUM (
  'Sorvete',
  'Acai',
  'Embalagens',
  'Alimentos',
  'Bebidas',
  'Higiene',
  'Eletronicos',
  'Vestuarios',
  'Papelaria',
  'OUTROS'
);

CREATE TYPE tipo_movimentacao_enum AS ENUM (
  'ENTRADA',
  'SAIDA',
  'AJUSTE',
  'PERDA',
  'DEVOLUCAO',
  'TRANSFERENCIA'
);

CREATE TYPE unidade_medida_enum AS ENUM (
  	'LITROS',
    'QUILOGRAMAS',
    'GRAMAS',
    'UNIDADES',
    'CAIXAS',
    'PACOTES',
    'METROS',
    'MILILITROS'
);

CREATE TABLE produtos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    codigo_barras VARCHAR(50) UNIQUE,
    preco_custo NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    preco_venda NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    quantidade_estoque INTEGER NOT NULL DEFAULT 0,
    unidade_medida VARCHAR (50),
    categoria VARCHAR (50),
    perecivel BOOLEAN NOT NULL DEFAULT FALSE,
    data_validade DATE,
    fornecedor VARCHAR(200),
    usuario_id UUID,               -- quem cadastrou / dono do registro
    usuario_nome VARCHAR(200),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW()
);
-- Índices úteis
CREATE INDEX idx_produtos_codigo_barras ON produtos(codigo_barras);
CREATE INDEX idx_produtos_nome ON produtos(nome_produto);

-- 5) Tabela estoque_fabrica
DROP TABLE IF EXISTS estoque_fabrica CASCADE;

CREATE TABLE estoque_fabrica (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_id UUID NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    quantidade_disponivel INTEGER NOT NULL DEFAULT 0,
    quantidade_minima INTEGER DEFAULT 0,
    quantidade_maxima INTEGER DEFAULT 1000,
	localizacao VARCHAR(200),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(produto_id)  -- Um produto só pode ter um registro de estoque na fábrica
);

CREATE INDEX idx_estoque_fabrica_produto ON estoque_fabrica(produto_id);


-- 6) Tabela estoque_loja
DROP TABLE IF EXISTS estoque_loja CASCADE;


CREATE TABLE estoque_loja (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_id UUID NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    quantidade_disponivel INTEGER NOT NULL DEFAULT 0,
    quantidade_minima INTEGER DEFAULT 0,
    quantidade_maxima INTEGER DEFAULT 100,
    localizacao VARCHAR(200),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(produto_id)
);

CREATE INDEX idx_estoque_loja_produto ON estoque_loja(produto_id);

CREATE TABLE lote_fabricacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_id UUID NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    codigo_lote VARCHAR(50) UNIQUE NOT NULL,
    quantidade_fabricada INTEGER NOT NULL,
    quantidade_disponivel INTEGER NOT NULL,
    data_fabricacao DATE NOT NULL,
    data_validade DATE,
    observacao TEXT,
    usuario_id UUID,
    usuario_nome VARCHAR(200),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_lote_produto ON lote_fabricacao(produto_id);
CREATE INDEX idx_lote_codigo ON lote_fabricacao(codigo_lote);
CREATE INDEX idx_lote_validade ON lote_fabricacao(data_validade);


-- 7) Tabela movimentacao_fabrica
DROP TABLE IF EXISTS movimentacao_fabrica CASCADE;

CREATE TABLE movimentacao_fabrica (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    estoque_fabrica_id UUID NOT NULL REFERENCES estoque_fabrica(id) ON DELETE CASCADE,
    produto_id UUID NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    lote_fabricacao_id UUID REFERENCES lote_fabricacao(id) ON DELETE SET NULL,
    tipo_movimentacao VARCHAR(50) NOT NULL,
    quantidade INTEGER NOT NULL,
    observacao TEXT,
    data_movimentacao TIMESTAMP NOT NULL DEFAULT NOW(),
    usuario_id UUID,
    usuario_nome VARCHAR(200),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movimentacao_fabrica_produto ON movimentacao_fabrica(produto_id);
CREATE INDEX idx_movimentacao_fabrica_lote ON movimentacao_fabrica(lote_fabricacao_id);
CREATE INDEX idx_movimentacao_fabrica_tipo ON movimentacao_fabrica(tipo_movimentacao);
CREATE INDEX idx_movimentacao_fabrica_data ON movimentacao_fabrica(data_movimentacao);

-- 8) Tabela movimentacao_loja
DROP TABLE IF EXISTS movimentacao_loja CASCADE;

CREATE TABLE movimentacao_loja (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    estoque_loja_id UUID NOT NULL REFERENCES estoque_loja(id) ON DELETE CASCADE,
    produto_id UUID NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    tipo_movimentacao VARCHAR(50) NOT NULL,
    quantidade INTEGER NOT NULL,
    observacao TEXT,
    data_movimentacao TIMESTAMP NOT NULL DEFAULT NOW(),
    usuario_id UUID,
    usuario_nome VARCHAR(200),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices para melhorar performance
CREATE INDEX idx_movimentacao_loja_estoque ON movimentacao_loja(estoque_loja_id);
CREATE INDEX idx_movimentacao_loja_produto ON movimentacao_loja(produto_id);
CREATE INDEX idx_movimentacao_loja_tipo ON movimentacao_loja(tipo_movimentacao);
CREATE INDEX idx_movimentacao_loja_data ON movimentacao_loja(data_movimentacao);
CREATE INDEX idx_movimentacao_loja_usuario ON movimentacao_loja(usuario_id);

CREATE OR REPLACE FUNCTION atualizar_data_atualizacao()
RETURNS TRIGGER AS $$
BEGIN
  NEW.data_atualizacao = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- attach trigger to tables with data_atualizacao
CREATE TRIGGER trg_produtos_update BEFORE UPDATE ON produtos
FOR EACH ROW EXECUTE FUNCTION atualizar_data_atualizacao();

CREATE TRIGGER trg_estoque_fabrica_update BEFORE UPDATE ON estoque_fabrica
FOR EACH ROW EXECUTE FUNCTION atualizar_data_atualizacao();

CREATE TRIGGER trg_estoque_loja_update BEFORE UPDATE ON estoque_loja
FOR EACH ROW EXECUTE FUNCTION atualizar_data_atualizacao();

CREATE TRIGGER trg_mov_fabrica_update BEFORE UPDATE ON movimentacao_fabrica
FOR EACH ROW EXECUTE FUNCTION atualizar_data_atualizacao();

CREATE TRIGGER trg_mov_loja_update BEFORE UPDATE ON movimentacao_loja
FOR EACH ROW EXECUTE FUNCTION atualizar_data_atualizacao();

