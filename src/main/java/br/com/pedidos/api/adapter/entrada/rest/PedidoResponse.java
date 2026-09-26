package br.com.pedidos.api.adapter.entrada.rest;

import br.com.pedidos.api.domain.Pedido;

import java.math.BigDecimal;
import java.util.List;

public record PedidoResponse(
        String id,
        String clienteId,
        List<ItemResponse> itens,
        String status,
        BigDecimal total) {

    public static PedidoResponse from(Pedido pedido) {
        var itens = pedido.itens().stream()
                .map(item -> new ItemResponse(item.produto(), item.quantidade(), item.precoUnitario()))
                .toList();
        return new PedidoResponse(
                pedido.id().toString(),
                pedido.cliente(),
                itens,
                pedido.status().name(),
                pedido.total());
    }

    public record ItemResponse(String sku, int quantidade, BigDecimal precoUnitario) {
    }
}
