package br.com.pedidos.api.application;

import java.util.UUID;

public class PedidoNaoEncontradoException extends RuntimeException {
    public PedidoNaoEncontradoException(UUID id) {
        super("pedido não encontrado: " + id);
    }
}
