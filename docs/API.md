# Referência da API

URL local padrão:

```text
http://localhost:8080
```

Todos os corpos de requisição e resposta usam JSON. Datas seguem ISO 8601.

## Itens

### Importar itens em lote

```http
POST /api/itens/batch
Content-Type: application/json
```

Corpo:

```json
{
  "itens": [
    {
      "nome": "Inclua vegetais no almoço",
      "tipo": "DICA"
    },
    {
      "nome": "Salada de grão-de-bico",
      "tipo": "RECEITA"
    }
  ]
}
```

Resposta de sucesso: `200 OK`, sem corpo.

Os campos `nome` e `tipo` de cada item são obrigatórios. O tipo deve ser `DICA` ou `RECEITA`. A operação não elimina duplicidades e não atualiza itens existentes.

Exemplo com cURL:

```bash
curl -X POST http://localhost:8080/api/itens/batch \
  -H "Content-Type: application/json" \
  -d '{"itens":[{"nome":"Inclua vegetais no almoço","tipo":"DICA"}]}'
```

### Listar itens por tipo e mês

```http
GET /api/itens?tipo=DICA&anoMes=2026-09
```

Resposta `200 OK`:

```json
[
  {
    "id": 1,
    "nome": "Inclua vegetais no almoço",
    "tipo": "DICA",
    "curtidoNoMes": true
  },
  {
    "id": 2,
    "nome": "Planeje suas refeições",
    "tipo": "DICA",
    "curtidoNoMes": false
  }
]
```

Parâmetros obrigatórios:

| Nome | Tipo | Exemplo | Observação |
| --- | --- | --- | --- |
| `tipo` | enum | `DICA` | Aceita `DICA` ou `RECEITA` |
| `anoMes` | string | `2026-09` | Convenção recomendada: `AAAA-MM` |

### Alternar curtida mensal

```http
POST /api/itens/1/toggle-curtida
Content-Type: application/json
```

Corpo:

```json
{
  "anoMes": "2026-09"
}
```

Resposta de sucesso: `200 OK`, sem corpo.

Se ainda não existir uma curtida para o par item/mês, a API cria o registro como curtido. Se já existir, inverte o valor atual. Um `id` inexistente retorna `404 Not Found`.

## Tarefas diárias

### Listar tarefas em uma data

```http
GET /api/tarefas-diarias?data=2026-09-01
```

Resposta `200 OK`:

```json
[
  {
    "id": 1,
    "nome": "Beber água",
    "categoria": "AGUA",
    "concluido": true
  },
  {
    "id": 2,
    "nome": "Realizar treino",
    "categoria": "TREINO",
    "concluido": false
  }
]
```

O parâmetro `data` é obrigatório e deve estar no formato `AAAA-MM-DD`. Categorias possíveis: `TREINO`, `CORRIDA`, `POSTAGEM`, `AGUA` e `OUTRO`.

### Alternar conclusão diária

```http
POST /api/tarefas-diarias/1/toggle-conclusao
Content-Type: application/json
```

Corpo:

```json
{
  "dataRegistro": "2026-09-01"
}
```

Resposta de sucesso: `200 OK`, sem corpo.

Se ainda não existir histórico para o par tarefa/data, a API cria o registro como concluído. Se já existir, inverte o valor atual. Um `id` inexistente retorna `404 Not Found`.

## Progresso mensal

### Consultar resumo

```http
GET /api/progresso/2026-09
```

Resposta `200 OK`:

```json
{
  "anoMes": "2026-09",
  "dicasCurtidas": 8,
  "receitasCurtidas": 5,
  "totalDicas": 25,
  "totalReceitas": 25
}
```

No estado atual, `totalDicas` e `totalReceitas` são metas fixas definidas no serviço, não contagens da tabela de itens.

## Erros

Recursos inexistentes e alguns erros de parâmetro são retornados no formato Problem Details. Exemplo de item inexistente:

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Item não encontrado",
  "instance": "/api/itens/999/toggle-curtida"
}
```

Exemplo de parâmetro com tipo inválido:

```json
{
  "type": "urn:bitt:erro:parametro-invalido",
  "title": "Erro de tipagem no parâmetro",
  "status": 400,
  "detail": "Parâmetro inválido: tipo",
  "instance": "/api/itens",
  "timestamp": "2026-09-01T12:00:00Z"
}
```

Erros de validação de corpo também retornam `400 Bad Request`, usando a resposta padrão do Spring Boot.
