CREATE TABLE tb_usuario (
                            id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            nome       VARCHAR(100)        NOT NULL,
                            email      VARCHAR(150) UNIQUE NOT NULL,
                            senha      VARCHAR(255)        NOT NULL,
                            created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE tb_lancamento (
                               id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               usuario_id UUID           NOT NULL
                                   REFERENCES tb_usuario(id) ON DELETE CASCADE,
                               tipo       VARCHAR(20)    NOT NULL
                                   CHECK (tipo IN ('GASTO','INVESTIMENTO','ENTRADA')),
                               descricao  VARCHAR(200)   NOT NULL,
                               valor      NUMERIC(15, 2) NOT NULL CHECK (valor > 0),
                               categoria  VARCHAR(50),
                               data       DATE           NOT NULL,
                               created_at TIMESTAMP DEFAULT NOW()
);
