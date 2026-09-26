package br.com.pedidos.api.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Pedido(String cliente, UUID id, StatusPedido status, List<ItemPedido> itens) {

    public Pedido {
        Objects.requireNonNull(id, "id não pode ser nulo");
        Objects.requireNonNull(status, "status não pode ser nulo");
        Objects.requireNonNull(itens, "itens não pode ser nulo");
        itens = List.copyOf(itens);
    }

    public Pedido(UUID id, StatusPedido status, List<ItemPedido> itens) {
        this(null, id, status, itens);
    }

    public static Pedido novo() {
        return new Pedido(null, UUID.randomUUID(), StatusPedido.ABERTO, List.of());
    }

    public static Pedido novo(String cliente) {
        Objects.requireNonNull(cliente, "cliente não pode ser nulo");
        if (cliente.isBlank()) {
            throw new IllegalArgumentException("cliente não pode ser vazio");
        }
        return new Pedido(cliente, UUID.randomUUID(), StatusPedido.ABERTO, List.of());
    }

    public Pedido adicionarItem(ItemPedido item) {
        Objects.requireNonNull(item, "item não pode ser nulo");
        exigirAberto();

        var novosItens = new ArrayList<>(itens);
        novosItens.add(item);
        return new Pedido(cliente, id, status, novosItens);
    }

    public Pedido pagar() {
        exigirAberto();
        return new Pedido(cliente, id, StatusPedido.PAGO, itens);
    }

    public Pedido cancelar() {
        exigirAberto();
        return new Pedido(cliente, id, StatusPedido.CANCELADO, itens);
    }

    public BigDecimal total() {
        return itens.stream()
                .map(item -> item.precoUnitario().multiply(BigDecimal.valueOf(item.quantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void exigirAberto() {
        if (status != StatusPedido.ABERTO) {
            throw new IllegalStateException("pedido deve estar aberto");
        }
    }
}
