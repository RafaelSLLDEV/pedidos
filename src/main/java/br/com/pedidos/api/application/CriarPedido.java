package br.com.pedidos.api.application;

import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;

import java.util.List;

public interface CriarPedido {

    Pedido criar(String cliente, List<ItemPedido> itens);
}
