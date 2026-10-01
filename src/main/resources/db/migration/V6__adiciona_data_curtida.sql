ALTER TABLE tb_curtida_mensal ADD COLUMN data_curtida DATE;
CREATE INDEX idx_curtida_data ON tb_curtida_mensal(data_curtida);
