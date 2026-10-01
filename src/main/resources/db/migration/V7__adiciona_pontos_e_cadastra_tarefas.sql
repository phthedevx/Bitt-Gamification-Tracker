ALTER TABLE tb_tarefa_diaria ADD COLUMN IF NOT EXISTS pontos INTEGER NOT NULL DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Humor/Diário') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Humor/Diário', 'OUTRO', true, 15);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Corpo e Mente') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Corpo e Mente', 'OUTRO', true, 10);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Ficha de Treino') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Ficha de Treino', 'TREINO', true, 15);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Check-in Academia') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Check-in Academia', 'TREINO', true, 50);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Corrida 3km+') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Corrida 3km+', 'CORRIDA', true, 50);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Vídeo Treino') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Vídeo Treino', 'TREINO', true, 50);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Post no Feed') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Post no Feed', 'POSTAGEM', true, 20);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Post Insta Stories') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Post Insta Stories', 'POSTAGEM', true, 30);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Curtidas Feitas') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Curtidas Feitas', 'OUTRO', true, 50);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Artigos Curtidos') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Artigos Curtidos', 'OUTRO', true, 50);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tb_tarefa_diaria WHERE nome = 'Curtidas Recebidas') THEN
        INSERT INTO tb_tarefa_diaria (nome, categoria, ativo, pontos) VALUES ('Curtidas Recebidas', 'OUTRO', true, 50);
    END IF;
END $$;
