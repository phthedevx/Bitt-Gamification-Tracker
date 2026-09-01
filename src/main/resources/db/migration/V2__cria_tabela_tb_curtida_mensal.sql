CREATE TABLE tb_curtida_mensal (
    id SERIAL PRIMARY KEY,
    item_id INTEGER NOT NULL,
    ano_mes VARCHAR(7) NOT NULL,
    curtido BOOLEAN NOT NULL DEFAULT FALSE,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_curtida_item FOREIGN KEY (item_id) REFERENCES tb_item(id),
    CONSTRAINT uk_item_ano_mes UNIQUE (item_id, ano_mes)
);
