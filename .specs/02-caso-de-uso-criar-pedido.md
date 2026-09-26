# Caso de uso: criar pedido

## Contexto

Com o domínio inicial de pedidos disponível, a aplicação precisa coordenar a criação de um pedido sem conhecer Spring, JPA, HTTP ou qualquer adapter. O caso de uso recebe um cliente e os itens desejados, cria o pedido e delega seu armazenamento a uma porta de saída.

## Tarefa

Definir o contrato do caso de uso para criar pedidos:

- a porta de entrada chama-se `CriarPedido`;
- a implementação da porta de entrada chama-se `CriarPedidoService`;
- a porta de saída chama-se `Pedidos`;
- o caso de uso recebe cliente e itens;
- uma lista vazia de itens é recusada;
- um pedido é criado a partir dos dados recebidos;
- o pedido criado é salvo por meio de `Pedidos`;
- o pedido devolvido é o pedido retornado por `Pedidos`.

## Regras

- O fluxo deve recusar a criação quando a lista de itens estiver vazia.
- O fluxo deve encaminhar cliente e itens válidos para a criação do domínio.
- O armazenamento deve ocorrer exclusivamente pela interface `Pedidos`.
- O resultado do caso de uso deve ser exatamente o pedido devolvido pela porta `Pedidos`.
- A implementação deve estar na camada de aplicação e não pode importar Spring, Spring Boot, JPA, HTTP ou classes de adapters.
- A camada de aplicação não cria Controller, DTO, configuração ou infraestrutura.
- A aplicação deve depender de abstrações do domínio e das portas, seguindo arquitetura hexagonal.
- Não devem ser adicionadas dependências ao projeto.

## Definição de pronto

Os testes devem cobrir:

1. Com cliente e uma lista não vazia de itens, o caso de uso cria um pedido.
2. O pedido criado é enviado à implementação de `Pedidos` para ser salvo.
3. O resultado devolvido pelo caso de uso é o mesmo objeto retornado por `Pedidos`.
4. Uma lista vazia de itens é recusada.
5. Quando a lista é vazia, `Pedidos` não é chamado.
6. O teste usa uma implementação em memória de `Pedidos`, localizada somente no código de teste.
7. Os imports da aplicação não incluem Spring, JPA, HTTP ou adapters.

Os testes devem ser executados pelo Maven Wrapper. A implementação não deve alterar `pom.xml`, criar Controller, JPA, DTOs, configuração, infraestrutura ou commit.

## Ambiguidade a decidir antes da implementação

O domínio atual de `Pedido` contém UUID, status e itens, mas não contém cliente. Antes de implementar, deve ser decidido se o domínio será ampliado para armazenar o cliente e qual será seu tipo/contrato, ou se o cliente será apenas um dado de entrada da aplicação sem ser persistido no pedido.
