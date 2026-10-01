# Implementação — Curtidas Mensais e Progresso Diário

## 1. Resumo
A implementação corrigiu as regras de negócio para separar os conceitos de "estado mensal de curtida" e "progresso diário". Anteriormente, o backend calculava o progresso como um mero totalizador de todas as curtidas do mês, e a operação de curtir funcionava como um "toggle", o que causava redução indevida de progresso e números superiores à meta diária.

## 2. Regra implementada
- **Curtida Mensal**: Registrar a primeira curtida do conteúdo dentro da competência (ex: 2026-09) tornou-se imutável. Uma vez curtido, o status não volta para `false`. É uma operação idempotente.
- **Progresso Diário**: O progresso passa a ser calculado utilizando a **data da curtida** (`data_curtida`). Apenas a **primeira curtida** gerada em uma data específica pontua no progresso daquele dia.
- Dicas e Receitas mantêm contadores independentes, limitados teoricamente à meta de 25 por dia.

## 3. Modelagem anterior
A tabela `tb_curtida_mensal` armazenava:
- `id`
- `item_id`
- `ano_mes`
- `curtido`
Sem registrar quando o evento ocorreu.

## 4. Modelagem nova
Adicionada a coluna `data_curtida DATE` na entidade `CurtidaMensal` e respectiva coluna no banco. O campo `curtido` foi mantido por compatibilidade histórica e para facilitar as queries existentes de listagem, mas agora ele nunca será revertido para `false`.

## 5. Migration criada
Foi criada a migration `V6__adiciona_data_curtida.sql`:
```sql
ALTER TABLE tb_curtida_mensal ADD COLUMN data_curtida DATE;
CREATE INDEX idx_curtida_data ON tb_curtida_mensal(data_curtida);
```

## 6. Tratamento dos dados legados
Registros legados permanecem com `data_curtida = NULL` e `curtido = true`.
- **Impacto no Progresso**: Como a query de progresso diário exige `dataCurtida = :data`, registros legados serão ignorados na contagem diária, evitando pontuações retroativas incorretas.
- **Impacto na Listagem**: A listagem continua buscando por `anoMes` e `curtido = true`, logo os conteúdos já curtidos continuarão aparecendo como curtidos normalmente no frontend.

## 7. Curtida mensal imutável
A lógica no `ItemService.curtir` agora verifica se a curtida já existe. Se existir e já for `true`, nada é feito. Se for `false` (um dado legado possivelmente alterado antes dessa release), é migrado permanentemente para `true` e sua data de curtida preenchida com a data requisitada. 

## 8. Idempotência
Chamar `POST /api/itens/10/curtida` inúmeras vezes no mesmo dia:
- Não altera o estado (continua `true`).
- Não atualiza a `data_curtida` (preserva a primeira data).
- Mantém o progresso intacto, garantindo que o usuário só pontua 1 vez por competência.

## 9. Cálculo do progresso diário
A API de progresso agora recebe uma `data` específica (`LocalDate`), através do DTO `ProgressoDiarioDTO`.
A query utilizada:
`SELECT c.item.tipo, COUNT(c.id) FROM CurtidaMensal c WHERE c.dataCurtida = :data AND c.curtido = true GROUP BY c.item.tipo`

## 10. Contrato final da API
- **Curtir Conteúdo:**
`POST /api/itens/{id}/curtida`
```json
{
  "anoMes": "2026-09",
  "data": "2026-09-30"
}
```
- **Consultar Progresso:**
`GET /api/progresso?data=2026-09-30`
```json
{
  "data": "2026-09-30",
  "dicasCurtidas": 3,
  "receitasCurtidas": 0,
  "metaDicas": 25,
  "metaReceitas": 25
}
```
*(O endpoint antigo `/api/progresso/{anoMes}` e o `/api/itens/{id}/toggle-curtida` foram removidos em prol da consistência)*

## 11. Validações
Implementada proteção no Service: A data informada no body (ex: 2026-09-30) DEVE corresponder obrigatoriamente à competência requisitada (2026-09), prevenindo inconsistências temporais geradas pelo frontend. Lança `RegraDeNegocioException` se incompatível.

## 12. Tratamento de concorrência
A constraint original do banco `UNIQUE (item_id, ano_mes)` atua como guardião transacional. Tentativas simultâneas de registrar a "primeira curtida" irão colidir no insert de banco de dados, resultando na gravação de apenas 1 registro válido e inviabilizando dupla pontuação.

## 13. Testes adicionados
A suíte `ItemServiceTest.java` cobre integralmente:
1. Primeira curtida do mês
2. Curtida repetida no mesmo dia (Idempotência)
3. Curtida repetida no dia seguinte
4. Mesma dica em outra competência
5. Validação de data incompatível (ex: 2026-10-01 em 2026-09)
6. Cálculo de progresso diário independente por `TipoItem`.

## 14. Resultado dos testes
Todos os 7 testes (incluindo o inicial) finalizados com 100% de aprovação (0 falhas).

## 15. Resultado do build
Build e Migrations executados com êxito sem warnings de JPA ou Dialect.

## 16. Arquivos alterados
- `V6__adiciona_data_curtida.sql`
- `CurtidaMensal.java`
- `CurtidaMensalRepository.java`
- `ItemService.java`
- `ItemController.java`
- `ProgressoController.java`
- `CurtidaRequestDTO.java` (Criado)
- `ProgressoDiarioDTO.java` (Criado)
- `CurtidaToggleDTO.java` (Removido)
- `ProgressoMensalDTO.java` (Removido)
- `ItemServiceTest.java` (Criado)

## 17. Commits realizados
(Os commits atômicos foram estruturados conforme as diretrizes)

## 18. Limitações ou decisões arquiteturais
- O valor da meta diária ("25") permaneceu hardcoded nos retornos de API (`ProgressoDiarioDTO`), por não haver ainda um módulo de parametrização global configurável. Pode ser isolado em `application.yml` ou Tabela de Configurações futuramente.
- Não existe suporte a multitenancy (`usuario_id`), então todas as curtidas são sistêmicas. Isso deve ser endereçado em próxima release de evolução arquitetural.
