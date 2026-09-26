package br.com.pedidos.api.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;
import br.com.pedidos.api.domain.StatusPedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class PedidosJpaAdapterIT {

    @Autowired
    private PedidosJpaAdapter adapter;

    @Test
    void salvaECarregaPedidoEmTransacoesSeparadas() {
        var pedido = Pedido.novo("c-1")
                .adicionarItem(new ItemPedido("CAFE-500", 2, new BigDecimal("18.90")));

        var salvo = adapter.salvar(pedido);
        var recuperado = adapter.buscarPorId(salvo.id()).orElseThrow();

        assertNotNull(recuperado.id());
        assertEquals(pedido.id(), recuperado.id());
        assertEquals(StatusPedido.ABERTO, recuperado.status());
        assertEquals(1, recuperado.itens().size());
        assertEquals("CAFE-500", recuperado.itens().get(0).produto());
        assertEquals(2, recuperado.itens().get(0).quantidade());
        assertEquals(0, new BigDecimal("18.90").compareTo(recuperado.itens().get(0).precoUnitario()));
        assertEquals(0, new BigDecimal("37.80").compareTo(recuperado.total()));
    }
}
