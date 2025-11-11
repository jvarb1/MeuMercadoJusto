-- ============================================
-- Script SQL para criar tabelas no Supabase
-- ============================================
-- Execute este script no SQL Editor do Supabase
-- Acesse: Seu Projeto > SQL Editor > New Query

-- 1. Criar tabela de produtos
CREATE TABLE IF NOT EXISTS produtos (
    id SERIAL PRIMARY KEY,
    nome TEXT NOT NULL,
    categoria TEXT NOT NULL,
    preco DOUBLE PRECISION NOT NULL,
    unidade TEXT NOT NULL,
    descricao TEXT DEFAULT '',
    imagem_url TEXT DEFAULT '',
    em_estoque BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Criar tabela de usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    senha TEXT NOT NULL,
    nome TEXT NOT NULL,
    telefone TEXT DEFAULT '',
    endereco TEXT DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. Criar tabela de estabelecimentos
CREATE TABLE IF NOT EXISTS estabelecimentos (
    id SERIAL PRIMARY KEY,
    nome TEXT NOT NULL,
    endereco TEXT NOT NULL,
    cidade TEXT NOT NULL,
    estado TEXT NOT NULL,
    cep TEXT DEFAULT '',
    telefone TEXT DEFAULT '',
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    ativo BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Criar tabela de preços de produtos
CREATE TABLE IF NOT EXISTS precos_produtos (
    id SERIAL PRIMARY KEY,
    produto_id INTEGER NOT NULL REFERENCES produtos(id) ON DELETE CASCADE,
    estabelecimento_id INTEGER NOT NULL REFERENCES estabelecimentos(id) ON DELETE CASCADE,
    preco DOUBLE PRECISION NOT NULL,
    data_atualizacao BIGINT NOT NULL,
    disponivel BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(produto_id, estabelecimento_id)
);

-- 5. Criar índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_produtos_categoria ON produtos(categoria);
CREATE INDEX IF NOT EXISTS idx_produtos_nome ON produtos(nome);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_estabelecimentos_cidade_estado ON estabelecimentos(cidade, estado);
CREATE INDEX IF NOT EXISTS idx_precos_produto_id ON precos_produtos(produto_id);
CREATE INDEX IF NOT EXISTS idx_precos_estabelecimento_id ON precos_produtos(estabelecimento_id);
CREATE INDEX IF NOT EXISTS idx_precos_produto_estabelecimento ON precos_produtos(produto_id, estabelecimento_id);

-- 6. Habilitar Row Level Security (RLS) - Opcional, mas recomendado
ALTER TABLE produtos ENABLE ROW LEVEL SECURITY;
ALTER TABLE usuarios ENABLE ROW LEVEL SECURITY;
ALTER TABLE estabelecimentos ENABLE ROW LEVEL SECURITY;
ALTER TABLE precos_produtos ENABLE ROW LEVEL SECURITY;

-- 7. Criar políticas de segurança (permitir leitura pública, escrita apenas autenticada)
-- Produtos: leitura pública
CREATE POLICY "Produtos são públicos para leitura"
    ON produtos FOR SELECT
    USING (true);

-- Estabelecimentos: leitura pública
CREATE POLICY "Estabelecimentos são públicos para leitura"
    ON estabelecimentos FOR SELECT
    USING (true);

-- Preços: leitura pública
CREATE POLICY "Preços são públicos para leitura"
    ON precos_produtos FOR SELECT
    USING (true);

-- Usuários: apenas o próprio usuário pode ver seus dados
CREATE POLICY "Usuários podem ver apenas seus próprios dados"
    ON usuarios FOR SELECT
    USING (auth.uid()::text = id::text);

-- 8. Criar função para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 9. Criar triggers para atualizar updated_at
CREATE TRIGGER update_produtos_updated_at BEFORE UPDATE ON produtos
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_usuarios_updated_at BEFORE UPDATE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_estabelecimentos_updated_at BEFORE UPDATE ON estabelecimentos
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_precos_produtos_updated_at BEFORE UPDATE ON precos_produtos
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- Dados de exemplo (opcional)
-- ============================================
-- Descomente as linhas abaixo para inserir dados de exemplo

/*
-- Inserir estabelecimentos de exemplo
INSERT INTO estabelecimentos (nome, endereco, cidade, estado, cep, telefone, latitude, longitude, ativo) VALUES
('Supermercado Bom Preço', 'Rua das Flores, 123', 'Arapiraca', 'AL', '57300-000', '(82) 3521-1234', -9.7520, -36.6612, true),
('Mercado Econômico', 'Av. Principal, 456', 'Arapiraca', 'AL', '57300-000', '(82) 3521-5678', -9.7500, -36.6600, true),
('Supermercado Popular', 'Rua da Paz, 789', 'Arapiraca', 'AL', '57300-000', '(82) 3521-9012', -9.7480, -36.6588, true),
('Atacadão', 'Av. Comercial, 1000', 'Arapiraca', 'AL', '57300-000', '(82) 3521-3456', -9.7550, -36.6620, true),
('Master Supermercado', 'Rua Central, 200', 'Arapiraca', 'AL', '57300-000', '(82) 3521-7890', -9.7530, -36.6615, true);
*/

