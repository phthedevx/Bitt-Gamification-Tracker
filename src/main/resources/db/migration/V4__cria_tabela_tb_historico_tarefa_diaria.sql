CREATE TABLE tb_historico_tarefa_diaria (
    id SERIAL PRIMARY KEY,
    tarefa_id INTEGER NOT NULL,
    data_registro DATE NOT NULL,
    concluido BOOLEAN NOT NULL DEFAULT FALSE,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historico_tarefa FOREIGN KEY (tarefa_id) REFERENCES tb_tarefa_diaria(id),
    CONSTRAINT uk_tarefa_data UNIQUE (tarefa_id, data_registro)
);
