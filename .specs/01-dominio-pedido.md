# Domínio inicial de pedido

## Contexto

O serviço precisa de um núcleo de domínio para representar pedidos sem depender de Spring, Spring Boot ou JPA. Esta primeira versão deve expressar as regras essenciais de um pedido, seus itens e seu ciclo de vida, mantendo o estado seguro contra alterações externas.

Na aula, o pedido `c-1` compra `CAFE-500`: 2 unidades a 18.90, totalizando 37.80.

## Tarefa

Definir o comportamento inicial de `Pedido`, `ItemPedido` e `StatusPedido` como records, com cópia defensiva das coleções e valores imutáveis.

O comportamento deve permitir criar um pedido vazio, adicionar itens por meio de uma nova versão do pedido, calcular o total a partir dos itens e avançar o pedido aberto para pago ou cancelado.

## Regras

- Um item só pode ser criado com quantidade positiva e preço unitário positivo.
- O preço unitário e o total usam `BigDecimal`.
- O total do pedido é sempre derivado da soma de `quantidade × preço unitário` de seus itens; não existe total independente que possa divergir dos itens.
- Criar um pedido novo produz um rascunho com UUID, status `ABERTO` e nenhum item.
- Adicionar um item produz outro objeto `Pedido`, preservando o pedido original e incluindo o novo item na nova versão.
- Um pedido com status `PAGO` ou `CANCELADO` recusa a adição de itens.
- As listas de itens não podem ser modificadas por código externo. A entrada e a saída devem usar cópia defensiva.
- Um pedido `ABERTO` pode ser pago, passando a `PAGO`, ou cancelado, passando a `CANCELADO`.
- Um pedido que não está `ABERTO` recusa qualquer nova transição de estado.
- `StatusPedido` deve representar, no mínimo, `ABERTO`, `PAGO` e `CANCELADO`.
- O domínio e a aplicação não importam nem dependem de Spring, Spring Boot ou JPA.
- A modelagem usa records e não usa Lombok.

## Definição de pronto

Cada regra deve ter um caso de teste automatizado, incluindo os erros:

1. Criar um pedido novo gera um UUID não nulo, status `ABERTO` e lista vazia.
2. Criar um item com quantidade positiva e preço positivo funciona.
3. Criar um item com quantidade zero falha.
4. Criar um item com quantidade negativa falha.
5. Criar um item com preço zero falha.
6. Criar um item com preço negativo falha.
7. O total de um pedido vazio é zero.
8. O total é calculado pela soma de quantidade vezes preço de cada item.
9. O cenário da aula funciona: `c-1`, `CAFE-500`, 2 × 18.90 resulta em 37.80.
10. Adicionar um item devolve uma instância diferente, mantém o pedido anterior sem itens e inclui o item na nova instância.
11. Adicionar item a um pedido `PAGO` falha.
12. Adicionar item a um pedido `CANCELADO` falha.
13. Tentar alterar a lista de itens recebida pelo pedido falha e não altera o pedido.
14. Alterar uma lista usada como entrada após a criação não altera os itens armazenados no pedido.
15. Pagar um pedido `ABERTO` produz um pedido `PAGO`.
16. Cancelar um pedido `ABERTO` produz um pedido `CANCELADO`.
17. Pagar um pedido `PAGO` falha.
18. Cancelar um pedido `PAGO` falha.
19. Pagar um pedido `CANCELADO` falha.
20. Cancelar um pedido `CANCELADO` falha.
21. Uma verificação de arquitetura confirma que o domínio e a aplicação não importam Spring, Spring Boot ou JPA.
22. Uma verificação de dependências confirma que a implementação não usa Lombok.

Os testes devem ser executados pelo Maven Wrapper, sem alterar `pom.xml`, arquivos Java fora do escopo aprovado ou infraestrutura.

## Ambiguidades para decidir

- A quantidade deve ser um número inteiro ou pode ser fracionária? Esta spec exige apenas que seja positiva.
- Quais tipos de exceção e mensagens devem representar entradas inválidas e transições recusadas?
- O `BigDecimal` deve ter escala padronizada (por exemplo, duas casas) e qual política de arredondamento deve ser usada?
- O identificador UUID deve ser gerado internamente sempre ou pode ser fornecido em alguma forma de reconstrução do pedido?
