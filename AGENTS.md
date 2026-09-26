# Regras do projeto

## Objetivo

Este serviço Spring Boot implementa a API de pedidos do projeto. As mudanças devem manter o código simples, testável e alinhado ao domínio de pedidos.

## Stack e comandos

- Use Java 21 e Spring Boot 4.1.1.
- Execute Maven sempre pelo Maven Wrapper adequado ao sistema operacional (`mvnw` ou `mvnw.cmd`).
- Não instale ferramentas, plugins ou dependências por conta própria.
- Não adicione nenhuma dependência nova sem pedir e receber autorização explícita.

## Arquitetura

- Organize o serviço segundo arquitetura hexagonal (ports and adapters).
- O domínio e a camada de aplicação não podem depender de Spring, Spring Boot ou JPA.
- Adaptadores de entrada e saída concentram integrações com frameworks, persistência e transporte.
- Mantenha as fronteiras entre domínio, aplicação e adaptadores explícitas.

## Regras de implementação

- Represente valores monetários com `BigDecimal`.
- Não use Lombok.
- Preserve as decisões e restrições registradas nas specs do projeto.

## Fluxo obrigatório para mudanças

1. Leia a spec indicada pelo pedido, quando houver uma.
2. Faça um plano curto antes de editar.
3. Aguarde o OK do usuário antes de editar arquivos.
4. Implemente somente o escopo aprovado.
5. Execute os testes apropriados pelo Maven Wrapper.
6. Mostre o diff das mudanças e relate eventuais bloqueios.

Não altere arquivos fora do escopo aprovado, não crie infraestrutura sem solicitação e não faça commits automaticamente.
