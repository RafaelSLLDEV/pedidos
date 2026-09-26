package br.com.pedidos.api.application;

import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;
import java.math.BigDecimal;
import java.util.UUID;

public class AdicionarItemService implements AdicionarItem {
    private final Pedidos pedidos;

    public AdicionarItemService(Pedidos pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public Pedido executar(UUID pedidoId, String sku, int quantidade, BigDecimal precoUnitario) {
        Pedido pedido = pedidos.buscarPorId(pedidoId)
                .orElseThrow(() -> new PedidoNaoEncontradoException(pedidoId));
        Pedido atualizado = pedido.adicionarItem(new ItemPedido(sku, quantidade, precoUnitario));
        return pedidos.salvar(atualizado);
    }
}
