# Checkpoint 4B: adapters REST e JPA

## Contexto

O domínio e a aplicação já definem `Pedido`, `ItemPedido`, `StatusPedido`, `CriarPedido`, `CriarPedidoService` e `Pedidos` sem dependências de framework. O próximo checkpoint introduz a persistência PostgreSQL por adapters, mantendo essas camadas intactas.

Esta spec registra três provas independentes: o banco local está disponível, o port `Pedidos` persiste e recupera o pedido por meio de um adapter JPA, e a futura entrada HTTP consegue chegar ao caso de uso. O checkpoint 4B implementa apenas a prova do banco e a persistência; nenhum endpoint HTTP será criado ainda.

## Tarefa

Preparar os adapters e suas provas para o pedido inicial:

- usar PostgreSQL 16 no compose já existente;
- configurar a aplicação para o Postgres local;
- usar entidades `PedidoJpaEntity` e `ItemJpaEntity`;
- usar um repository Spring Data;
- mapear manualmente entre domínio e persistência;
- implementar `PedidosJpaAdapter` para o port de saída `Pedidos`;
- manter domínio e aplicação sem imports de Spring, JPA, HTTP ou adapters;
- manter o total derivado dos itens e sem coluna `total`;
- preservar o UUID/String do domínio como identificador persistido, sem gerar outro ID;
- executar o adapter dentro de transação enquanto os itens ainda podem ser mapeados;
- manter `open-in-view=false`.

## Regras

### Prova 1: banco disponível

- O serviço deve usar a imagem PostgreSQL 16 definida em `infra/docker-compose.yml`.
- O banco, usuário e senha locais são `pedidos`.
- A porta local deve continuar configurável.
- A aplicação deve apontar para esse banco por `application.yml`.
- `spring.jpa.open-in-view` deve ser `false`.
- A prova deve confirmar que o PostgreSQL aceita conexões e responde a `SELECT 1`.

### Prova 2: persistência pelo adapter

- `PedidoJpaEntity` representa a tabela `pedido`.
- O identificador persistido é o mesmo UUID/String do domínio; não há geração de outro ID.
- `ItemJpaEntity` representa os itens relacionados ao pedido e armazena `sku`, `quantidade` e `precoUnitario`.
- Não existe coluna `total`; o total continua derivado pelos itens no domínio.
- O repository é uma porta técnica Spring Data para a entidade do pedido.
- O mapper é manual e converte domínio para entidade e entidade para domínio.
- `PedidosJpaAdapter` implementa o port de saída `Pedidos`.
- O adapter usa transação para materializar os itens antes de encerrar a sessão de persistência.
- O domínio e a aplicação permanecem sem imports de Spring, JPA, HTTP ou adapters.
- A persistência deve salvar o pedido `c-1` com `CAFE-500`, quantidade 2 e preço 18.90, e recuperá-lo por UUID em uma nova transação.
- A leitura deve comprovar itens, status `ABERTO` e total derivado `37.80`.

### Prova 3: HTTP

- Uma etapa posterior deverá expor uma entrada HTTP que invoque o port `CriarPedido`.
- O adapter HTTP não deve ser conhecido pelo domínio ou pela aplicação.
- O fluxo HTTP deverá preservar as regras de criação e persistência do caso de uso.
- Nenhum Controller, DTO ou teste HTTP faz parte do checkpoint 4B.

## Definição de pronto

No checkpoint 4B:

1. A dependência de Spring Data JPA e o PostgreSQL Driver estão disponíveis no `pom.xml`, sem outras dependências novas.
2. `application.yml` configura o Postgres local e `open-in-view=false`.
3. A prova do banco responde `pg_isready` e `SELECT 1`.
4. `PedidosJpaAdapterIT` executa explicitamente contra o Postgres do compose.
5. O teste salva o pedido `c-1` com `CAFE-500`, 2 × 18.90.
6. O teste inicia uma nova transação para recuperar o pedido pelo UUID.
7. O teste verifica itens, status `ABERTO` e total `37.80`.
8. Uma consulta direta à tabela `pedido` é apresentada como evidência.
9. O teste de contexto gerado é adaptado para a convenção `*IT`, preservado e executado explicitamente; ele não é apagado nem desativado.
10. Os testes unitários existentes são executados pelo Maven Wrapper sem depender do banco.
11. Nenhum endpoint HTTP, H2, Lombok, MapStruct, alteração no domínio/aplicação ou commit é criado.

## Prova HTTP futura

A prova HTTP será definida e implementada em checkpoint posterior, depois que a persistência estiver comprovada. Ela deverá demonstrar uma requisição de criação chegando ao caso de uso e retornando o resultado persistido, sem mover regras para o Controller.
## Contrato HTTP

### Criação de pedido

- Método e rota: `POST /pedidos`.
- O corpo deve ser JSON com `clienteId` e `itens`.
- Cada item deve conter `sku`, `quantidade` e `precoUnitario`.
- `clienteId` é obrigatório e não pode estar em branco.
- `itens` é obrigatório e não pode ser vazio.
- `sku`, `quantidade` e `precoUnitario` são obrigatórios em cada item.
- `quantidade` deve ser recebida como número inteiro e `precoUnitario` como número decimal JSON.
- `@Valid` verifica presença e formato; a regra de quantidade e preço positivos continua no domínio.
- O Controller chama somente o port `CriarPedido` e não conhece JPA, repository ou adapter de persistência.

Exemplo de requisição:

```json
{
  "clienteId": "c-1",
  "itens": [
    {
      "sku": "CAFE-500",
      "quantidade": 2,
      "precoUnitario": 18.90
    }
  ]
}
```

Uma criação válida responde `201 Created` com JSON contendo `id` como UUID, `clienteId`, `itens`, `status` igual a `ABERTO` e `total` como número decimal JSON:

```json
{
  "id": "<uuid>",
  "clienteId": "c-1",
  "itens": [
    {
      "sku": "CAFE-500",
      "quantidade": 2,
      "precoUnitario": 18.90
    }
  ],
  "status": "ABERTO",
  "total": 37.80
}
```

### Erros

- `ItemInvalidoException` responde `422 Unprocessable Entity` com uma mensagem legível.
- `PedidoSemItensException` responde `422 Unprocessable Entity` com uma mensagem legível.
- JSON malformado responde `400 Bad Request`.
- Campos obrigatórios ausentes ou inválidos para o formato respondem `400 Bad Request`.
- Uma quantidade zero deve atravessar a validação de formato e ser recusada pela regra do domínio, resultando em `422`.
- Uma lista vazia deve resultar em `422` e não pode adicionar linha ao banco.
- Erros recusados não podem persistir pedido nem item.

### Prova HTTP e reinício

A prova HTTP deve registrar, contra a aplicação iniciada com o Postgres local:

1. `CAFE-500`, quantidade 2 e preço `18.90` retorna `201` e total `37.80`.
2. Quantidade zero retorna `422`.
3. Lista vazia retorna `422`.
4. JSON malformado retorna `400`.
5. Após cada recusa, uma consulta confirma que nenhuma linha nova foi adicionada.
6. O UUID retornado no caso válido permite consultar o pedido e seus itens no banco.
7. Depois de reiniciar a aplicação, o mesmo UUID continua recuperável com os mesmos itens, status `ABERTO` e total `37.80`.
8. Ao final, somente o processo da aplicação iniciado para a prova é encerrado.