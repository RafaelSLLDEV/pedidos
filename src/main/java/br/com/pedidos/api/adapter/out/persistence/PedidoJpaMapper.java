package br.com.pedidos.api.adapter.out.persistence;

import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;

import java.util.List;

final class PedidoJpaMapper {

    PedidoJpaEntity toEntity(Pedido pedido) {
        var entity = new PedidoJpaEntity(pedido.id(), pedido.cliente(), pedido.status());
        pedido.itens().stream()
                .map(item -> new ItemJpaEntity(item.produto(), item.quantidade(), item.precoUnitario()))
                .forEach(entity::adicionarItem);
        return entity;
    }

    Pedido toDomain(PedidoJpaEntity entity) {
        var itens = entity.getItens().stream()
                .map(item -> new ItemPedido(item.getSku(), item.getQuantidade(), item.getPrecoUnitario()))
                .toList();
        return new Pedido(entity.getCliente(), entity.getId(), entity.getStatus(), itens);
    }
}
