package br.com.pedidos.api.application;

import br.com.pedidos.api.domain.Pedido;
import java.math.BigDecimal;
import java.util.UUID;

public interface AdicionarItem {
    Pedido executar(UUID pedidoId, String sku, int quantidade, BigDecimal precoUnitario);
}
