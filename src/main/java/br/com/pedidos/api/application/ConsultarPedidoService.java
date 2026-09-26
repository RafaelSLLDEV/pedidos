package br.com.pedidos.api.application;

import br.com.pedidos.api.domain.Pedido;

import java.util.Objects;
import java.util.UUID;

public final class ConsultarPedidoService implements ConsultarPedido {

    private final Pedidos pedidos;

    public ConsultarPedidoService(Pedidos pedidos) {
        this.pedidos = Objects.requireNonNull(pedidos, "pedidos não pode ser nulo");
    }

    @Override
    public Pedido porId(UUID pedidoId) {
        return pedidos.buscarPorId(Objects.requireNonNull(pedidoId, "pedidoId não pode ser nulo"))
                .orElseThrow(() -> new PedidoNaoEncontradoException(pedidoId));
    }
}
