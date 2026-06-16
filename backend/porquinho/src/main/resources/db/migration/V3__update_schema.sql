CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

CREATE TABLE tb_lancamento
(
    id         UUID           NOT NULL,
    usuario_id UUID           NOT NULL,
    tipo       VARCHAR(255)   NOT NULL,
    descricao  VARCHAR(255)   NOT NULL,
    valor      DECIMAL(15, 2) NOT NULL,
    categoria  VARCHAR(255),
    data       date           NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_tb_lancamento PRIMARY KEY (id)
);

CREATE TABLE tb_usuario
(
    id         UUID         NOT NULL,
    nome       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL,
    senha      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_tb_usuario PRIMARY KEY (id)
);

ALTER TABLE tb_usuario
    ADD CONSTRAINT uc_tb_usuario_email UNIQUE (email);

ALTER TABLE tb_lancamento
    ADD CONSTRAINT FK_TB_LANCAMENTO_ON_USUARIO FOREIGN KEY (usuario_id) REFERENCES tb_usuario (id);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);