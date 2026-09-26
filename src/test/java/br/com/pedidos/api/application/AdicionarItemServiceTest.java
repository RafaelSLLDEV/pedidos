package br.com.pedidos.api.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;
import br.com.pedidos.api.domain.StatusPedido;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class AdicionarItemServiceTest {

    private static final ItemPedido CAFE = new ItemPedido("CAFE-500", 2, new BigDecimal("18.90"));

    @Test
    void adicionaItemMantendoPedidoEAtualizaTotal() {
        var original = Pedido.novo("cliente-1").adicionarItem(CAFE);
        var repositorio = new MemoriaPedidos(original);

        var resultado = new AdicionarItemService(repositorio)
                .executar(original.id(), "PAO-001", 1, new BigDecimal("18.90"));

        assertEquals(original.id(), resultado.id());
        assertEquals("cliente-1", resultado.cliente());
        assertEquals(StatusPedido.ABERTO, resultado.status());
        assertEquals(new BigDecimal("56.70"), resultado.total());
        assertEquals(2, resultado.itens().size());
        assertEquals(1, repositorio.salvos.size());
    }

    @Test
    void pedidoInexistenteNaoSalva() {
        var repositorio = new MemoriaPedidos(null);

        assertThrows(PedidoNaoEncontradoException.class, () ->
                new AdicionarItemService(repositorio).executar(UUID.randomUUID(), "CAFE-500", 1, new BigDecimal("18.90")));
        assertEquals(0, repositorio.salvos.size());
    }

    @Test
    void pedidoFechadoNaoSalva() {
        var pago = Pedido.novo("cliente-1").adicionarItem(CAFE).pagar();
        var repositorio = new MemoriaPedidos(pago);

        assertThrows(IllegalStateException.class, () ->
                new AdicionarItemService(repositorio).executar(pago.id(), "PAO-001", 1, new BigDecimal("18.90")));
        assertEquals(0, repositorio.salvos.size());
    }

    @Test
    void itemInvalidoNaoSalva() {
        var pedido = Pedido.novo("cliente-1").adicionarItem(CAFE);
        var repositorio = new MemoriaPedidos(pedido);

        assertThrows(RuntimeException.class, () ->
                new AdicionarItemService(repositorio).executar(pedido.id(), "PAO-001", 0, new BigDecimal("18.90")));
        assertEquals(0, repositorio.salvos.size());
    }

    private static final class MemoriaPedidos implements Pedidos {
        private final Pedido encontrado;
        private final List<Pedido> salvos = new ArrayList<>();

        private MemoriaPedidos(Pedido encontrado) {
            this.encontrado = encontrado;
        }

        @Override
        public Optional<Pedido> buscarPorId(UUID id) {
            return Optional.ofNullable(encontrado).filter(pedido -> pedido.id().equals(id));
        }

        @Override
        public Pedido salvar(Pedido pedido) {
            salvos.add(pedido);
            return pedido;
        }
    }
}
