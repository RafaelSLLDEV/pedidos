package br.com.pedidos.api.application;

import br.com.pedidos.api.domain.Pedido;

import java.util.Optional;
import java.util.UUID;

public interface Pedidos {

    Optional<Pedido> buscarPorId(UUID id);

    Pedido salvar(Pedido pedido);
}
