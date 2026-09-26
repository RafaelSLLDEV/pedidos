# Caso de uso: adicionar item ao pedido

## Contexto

O pedido já pode ser criado por `POST /pedidos` e persistido pelo adapter JPA. Esta etapa acrescenta a operação de adicionar um item a um pedido existente, preservando as regras do domínio, a separação hexagonal e a consistência transacional.

A operação deve buscar o pedido pelo port `Pedidos`, aplicar o comportamento do domínio, salvar a nova versão pelo mesmo port e expor o resultado por HTTP.

## Tarefa

Definir o caso de uso e o contrato HTTP para adicionar um item:

- buscar o pedido pelo port `Pedidos`;
- quando o pedido não existir, gerar `PedidoNaoEncontradoException`;
- quando o pedido estiver fechado (`PAGO` ou `CANCELADO`), recusar a operação;
- quando quantidade ou preço forem inválidos, deixar a regra do domínio gerar `ItemInvalidoException`;
- adicionar o item ao pedido e atualizar o total derivado;
- salvar o pedido atualizado pelo port `Pedidos`;
- devolver `200 OK` em `POST /pedidos/{id}/itens`;
- devolver `404 Not Found` para pedido inexistente;
- devolver `409 Conflict` para pedido fechado;
- devolver `422 Unprocessable Entity` para item inválido;
- ampliar o handler REST existente sem mudar `CriarPedido`.

## Regras de comportamento

- A busca usa o UUID informado na rota e a operação não cria outro pedido.
- O pedido atualizado mantém exatamente o mesmo UUID.
- O pedido original continua imutável; adicionar item devolve outra versão do pedido.
- O total é recalculado pelos itens do domínio.
- O caso verificável parte de um pedido com total `37.80` e adiciona 1 unidade a `18.90`, resultando em `56.70`.
- O pedido salvo deve manter cliente, status `ABERTO` e todos os itens anteriores.
- Uma falha de busca, validação ou estado fechado não pode gravar pedido nem item.
- O Controller conhece somente o port de entrada do caso de uso e objetos HTTP; não conhece JPA, repository ou adapter de persistência.
- O handler REST existente concentra o mapeamento das exceções para HTTP.

## Contrato HTTP

### Requisição

- Método e rota: `POST /pedidos/{id}/itens`.
- `{id}` é o UUID do pedido.
- O corpo contém `sku`, `quantidade` e `precoUnitario`.
- `@Valid` verifica presença e formato.
- A regra de quantidade e preço positivos permanece no domínio.

Exemplo:

```json
{
  "sku": "CAFE-500",
  "quantidade": 1,
  "precoUnitario": 18.90
}
```

### Resposta de sucesso

A operação responde `200 OK` com o pedido atualizado, o mesmo UUID, status `ABERTO`, todos os itens e total decimal `56.70`.

### Respostas de erro

- `PedidoNaoEncontradoException`: `404 Not Found` com mensagem.
- Pedido `PAGO` ou `CANCELADO`: `409 Conflict` com mensagem.
- `ItemInvalidoException`, incluindo quantidade zero ou negativa e preço zero ou negativo: `422 Unprocessable Entity` com mensagem.
- JSON malformado ou campos obrigatórios ausentes seguem o tratamento `400 Bad Request` já existente.

## Casos de teste obrigatórios

1. Buscar pedido existente pelo UUID e devolver `200`.
2. Adicionar `CAFE-500`, quantidade 1 e preço 18.90 a um pedido de total 37.80.
3. Verificar total final 56.70.
4. Verificar que o UUID permanece exatamente igual.
5. Verificar que cliente, status `ABERTO` e itens anteriores permanecem.
6. Verificar que o pedido atualizado é salvo pelo port `Pedidos`.
7. Pedido inexistente retorna `404` e não chama o salvamento.
8. Pedido `PAGO` retorna `409` e não grava.
9. Pedido `CANCELADO` retorna `409` e não grava.
10. Quantidade zero retorna `422` e não grava.
11. Quantidade negativa retorna `422` e não grava.
12. Preço zero retorna `422` e não grava.
13. Preço negativo retorna `422` e não grava.
14. JSON malformado retorna `400`.
15. Campo obrigatório ausente retorna `400`.
16. Teste integrado confirma a linha e seus itens no PostgreSQL após a operação.
17. Teste HTTP confirma o mesmo comportamento através de `POST /pedidos/{id}/itens`.

## Limites de arquivos

A implementação posterior poderá criar ou alterar somente:

- `src/main/java/br/com/pedidos/api/application/AdicionarItem.java`;
- `src/main/java/br/com/pedidos/api/application/AdicionarItemService.java`;
- `src/main/java/br/com/pedidos/api/application/PedidoNaoEncontradoException.java`;
- `src/main/java/br/com/pedidos/api/application/Pedidos.java`, apenas para acrescentar a busca necessária;
- `src/main/java/br/com/pedidos/api/domain/Pedido.java`, somente se necessário para uma exceção de pedido fechado sem dependência de framework;
- `src/main/java/br/com/pedidos/api/adapter/entrada/rest/AdicionarItemRequest.java`;
- `src/main/java/br/com/pedidos/api/adapter/entrada/rest/PedidoController.java`;
- `src/main/java/br/com/pedidos/api/adapter/entrada/rest/PedidoExceptionHandler.java`;
- `src/main/java/br/com/pedidos/api/config/CasosDeUsoConfig.java`;
- testes unitários e de integração correspondentes em `src/test/java`.

Não alterar `pom.xml`, entidades JPA, mapper JPA, repository JPA, `application.yml`, `CriarPedido`, endpoints existentes ou infraestrutura. Não criar novos endpoints além de `POST /pedidos/{id}/itens`. Não fazer commit.