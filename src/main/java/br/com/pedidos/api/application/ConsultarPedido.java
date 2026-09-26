package br.com.pedidos.api.application;

import br.com.pedidos.api.domain.Pedido;

import java.util.UUID;

public interface ConsultarPedido {

    Pedido porId(UUID pedidoId);
}
