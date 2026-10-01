# Funcionalidades do Backend - Tracker

## Curtida Mensal
- **Regra de Negócio**: Um conteúdo (Dica ou Receita) só pode ser curtido **uma vez** por competência (Mês/Ano, ex: "2026-09"). 
- **Imutabilidade e Idempotência**: A curtida é uma operação aditiva. Uma vez curtido, o conteúdo permanece curtido durante toda a competência mensal e não pode ser descurtido ou revertido para `false`. Repetir a requisição de curtida manterá o status inalterado e não contará novamente no progresso.
- **Data da Primeira Curtida**: O backend grava a exata data em que o item foi curtido pela primeira vez (`data_curtida`). Isso é importante para atribuir o "ponto" ao dia correto.
- **Nova Competência**: Quando um novo mês iniciar, os conteúdos podem ser curtidos novamente sob essa nova competência, reiniciando o estado isoladamente.

## Progresso Diário (Evolução)
- **Regra de Negócio**: O painel de "Dicas X/25" e "Receitas X/25" conta **quantos conteúdos foram curtidos pela primeira vez NAQUELE DIA**.
- **Independência**: Dicas e Receitas possuem contadores independentes, limitados teoricamente à meta de 25 por dia. Se o usuário curte 3 dicas hoje, terá `3/25`. Amanhã, esse painel reinicia em `0/25`, embora as dicas de ontem permaneçam com seus coraçõezinhos vermelhos (`curtido=true`) até o final do mês.
- **Tratamento de Registros Legados**: Curtidas gravadas no sistema em versões anteriores não possuem `data_curtida`. Por proteção, elas são contadas normalmente nas listagens (como "já curtidas"), mas NÃO interferem na contagem de progresso diário, pois sua data real de evento é desconhecida.

## Endpoints e Payloads
- **POST `/api/itens/{id}/curtida`**
  - **Objetivo**: Registra a primeira curtida do item na competência.
  - **Body**: `{"anoMes": "2026-09", "data": "2026-09-30"}`
  - *Nota*: A data deve pertencer à mesma competência do anoMes, senão será rejeitada.

- **GET `/api/itens?tipo={tipo}&anoMes={anoMes}`**
  - **Objetivo**: Retorna a listagem de itens e seus status. Se o item já possuir um registro na tabela `tb_curtida_mensal` (competência correspondente) e este estiver `true`, ele será listado com `"curtido": true`.
  
- **GET `/api/progresso?data={data}`**
  - **Objetivo**: Calcula o número de curtidas *inéditas* daquela data (filtra diretamente por `data_curtida`).
  - **Response**: 
    ```json
    {
      "data": "2026-09-30",
      "dicasCurtidas": 3,
      "receitasCurtidas": 0,
      "metaDicas": 25,
      "metaReceitas": 25
    }
    ```
