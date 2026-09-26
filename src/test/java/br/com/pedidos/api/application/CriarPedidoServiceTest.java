package br.com.pedidos.api.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class CriarPedidoServiceTest {

    private static final ItemPedido CAFE = new ItemPedido("CAFE-500", 2, new BigDecimal("18.90"));

    @Test
    void criaPedidoSalvaEDevolvePedidoSalvo() {
        var repositorio = new MemoriaPedidos();
        var pedidoSalvo = Pedido.novo("pedido-salvo").adicionarItem(CAFE);
        repositorio.resultado = pedidoSalvo;
        var casoDeUso = new CriarPedidoService(repositorio);

        var resultado = casoDeUso.criar("cliente-1", List.of(CAFE));

        assertSame(pedidoSalvo, resultado);
        assertEquals(1, repositorio.salvos.size());
        assertEquals("cliente-1", repositorio.salvos.get(0).cliente());
        assertEquals(List.of(CAFE), repositorio.salvos.get(0).itens());
    }

    @Test
    void recusaListaVaziaSemSalvar() {
        var repositorio = new MemoriaPedidos();
        var casoDeUso = new CriarPedidoService(repositorio);

        assertThrows(IllegalArgumentException.class, () -> casoDeUso.criar("cliente-1", List.of()));
        assertEquals(0, repositorio.salvos.size());
    }

    private static final class MemoriaPedidos implements Pedidos {

        private final List<Pedido> salvos = new ArrayList<>();
        private Pedido resultado;

        @Override
        public Pedido salvar(Pedido pedido) {
            salvos.add(pedido);
            return resultado == null ? pedido : resultado;
        }
    }
}
