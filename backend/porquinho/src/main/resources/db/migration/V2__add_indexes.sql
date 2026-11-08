CREATE INDEX IF NOT EXISTS idx_lancamento_usuario   ON tb_lancamento(usuario_id);
CREATE INDEX IF NOT EXISTS idx_lancamento_data      ON tb_lancamento(data DESC);
CREATE INDEX IF NOT EXISTS idx_lancamento_tipo      ON tb_lancamento(tipo);
CREATE INDEX IF NOT EXISTS idx_lancamento_categoria ON tb_lancamento(categoria);
CREATE INDEX IF NOT EXISTS idx_usuario_email        ON tb_usuario(email);
