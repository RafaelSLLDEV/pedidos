package br.com.pedidos.api.application;

import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;

import java.util.List;
import java.util.Objects;

public final class CriarPedidoService implements CriarPedido {

    private final Pedidos pedidos;

    public CriarPedidoService(Pedidos pedidos) {
        this.pedidos = Objects.requireNonNull(pedidos, "pedidos não pode ser nulo");
    }

    @Override
    public Pedido criar(String cliente, List<ItemPedido> itens) {
        Objects.requireNonNull(itens, "itens não pode ser nulo");
        if (itens.isEmpty()) {
            throw new PedidoSemItensException("pedido deve conter itens");
        }

        var pedido = Pedido.novo(cliente);
        for (var item : itens) {
            pedido = pedido.adicionarItem(item);
        }
        return pedidos.salvar(pedido);
    }
}
