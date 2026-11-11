CREATE TABLE tb_investimento (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id        UUID            NOT NULL
        REFERENCES tb_usuario (id) ON DELETE CASCADE,
    simbolo           VARCHAR(20)     NOT NULL,           -- par na Binance, ex: BTCBRL
    quantidade        NUMERIC(30, 8)  NOT NULL CHECK (quantidade > 0),
    valor_inicial     NUMERIC(20, 8)  NOT NULL CHECK (valor_inicial > 0), -- preço unitário pago
    percentual_alerta NUMERIC(7, 2)   NOT NULL CHECK (percentual_alerta > 0),
    direcao           VARCHAR(10)     NOT NULL
        CHECK (direcao IN ('ALTA', 'QUEDA', 'AMBOS')),
    ativo             BOOLEAN         NOT NULL DEFAULT TRUE,
    notificado_em     TIMESTAMP,
    created_at        TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_investimento_usuario ON tb_investimento (usuario_id);
CREATE INDEX idx_investimento_ativo   ON tb_investimento (ativo) WHERE ativo;
