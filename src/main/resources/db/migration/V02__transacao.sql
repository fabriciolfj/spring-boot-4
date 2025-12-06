-- Criação da tabela
CREATE TABLE transacoes (
    id BIGSERIAL PRIMARY KEY,
    id_original VARCHAR(255) NOT NULL UNIQUE,
    data_hora TIMESTAMP NOT NULL,
    tipo VARCHAR(200) NOT NULL,
    valor DECIMAL(15, 2) NOT NULL,
    origem VARCHAR(255) NOT NULL,
    destino VARCHAR(255) NOT NULL,
    descricao VARCHAR(500),
    status VARCHAR(200) NOT NULL,
    processado_em TIMESTAMP NOT NULL,
    taxa_aplicada DECIMAL(15, 2),
    valor_liquido DECIMAL(15, 2)
);

-- Índices para melhorar performance
CREATE INDEX idx_transacoes_id_original ON transacoes(id_original);
CREATE INDEX idx_transacoes_data_hora ON transacoes(data_hora);
CREATE INDEX idx_transacoes_status ON transacoes(status);
CREATE INDEX idx_transacoes_tipo ON transacoes(tipo);
CREATE INDEX idx_transacoes_origem ON transacoes(origem);
CREATE INDEX idx_transacoes_destino ON transacoes(destino);

-- Índice composto para consultas comuns
CREATE INDEX idx_transacoes_status_data ON transacoes(status, data_hora DESC);

-- Comentários na tabela e colunas
COMMENT ON TABLE transacoes IS 'Armazena transações financeiras processadas';
COMMENT ON COLUMN transacoes.id_original IS 'Identificador original da transação do sistema externo';
COMMENT ON COLUMN transacoes.valor_liquido IS 'Valor após aplicação de taxas';