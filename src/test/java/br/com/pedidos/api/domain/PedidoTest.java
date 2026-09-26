package br.com.pedidos.api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class PedidoTest {

    private static final ItemPedido CAFE = new ItemPedido("CAFE-500", 2, new BigDecimal("18.90"));

    @Test
    void novoCriaPedidoAbertoComUuidEListaVazia() {
        var pedido = Pedido.novo();

        assertNotNull(pedido.id());
        assertEquals(StatusPedido.ABERTO, pedido.status());
        assertEquals(List.of(), pedido.itens());
    }

    @Test
    void totalDePedidoVazioEZero() {
        assertEquals(BigDecimal.ZERO, Pedido.novo().total());
    }

    @Test
    void totalECalculadoPelosItens() {
        var pedido = Pedido.novo()
                .adicionarItem(CAFE)
                .adicionarItem(new ItemPedido("PAO-001", 1, new BigDecimal("7.10")));

        assertEquals(new BigDecimal("44.90"), pedido.total());
    }

    @Test
    void exemploDaAulaTotalizaTrintaESeteEoitenta() {
        var pedido = Pedido.novo().adicionarItem(CAFE);

        assertEquals(new BigDecimal("37.80"), pedido.total());
    }

    @Test
    void adicionarItemDevolveOutroPedidoEPreservaOAnterior() {
        var original = Pedido.novo();
        var atualizado = original.adicionarItem(CAFE);

        assertNotEquals(original, atualizado);
        assertEquals(List.of(), original.itens());
        assertEquals(List.of(CAFE), atualizado.itens());
    }

    @Test
    void listaDeItensNaoPodeSerAlteradaPorFora() {
        var itens = new ArrayList<ItemPedido>();
        var pedido = new Pedido(java.util.UUID.randomUUID(), StatusPedido.ABERTO, itens);

        itens.add(CAFE);

        assertEquals(List.of(), pedido.itens());
        assertThrows(UnsupportedOperationException.class, () -> pedido.itens().add(CAFE));
    }

    @Test
    void pedidoPagoNaoAceitaItem() {
        var pedido = Pedido.novo().pagar();

        assertThrows(IllegalStateException.class, () -> pedido.adicionarItem(CAFE));
    }

    @Test
    void pedidoCanceladoNaoAceitaItem() {
        var pedido = Pedido.novo().cancelar();

        assertThrows(IllegalStateException.class, () -> pedido.adicionarItem(CAFE));
    }

    @Test
    void pedidoAbertoPodeSerPago() {
        assertEquals(StatusPedido.PAGO, Pedido.novo().pagar().status());
    }

    @Test
    void pedidoAbertoPodeSerCancelado() {
        assertEquals(StatusPedido.CANCELADO, Pedido.novo().cancelar().status());
    }

    @Test
    void pedidoPagoNaoPodeSerPagoNovamente() {
        assertThrows(IllegalStateException.class, () -> Pedido.novo().pagar().pagar());
    }

    @Test
    void pedidoPagoNaoPodeSerCancelado() {
        assertThrows(IllegalStateException.class, () -> Pedido.novo().pagar().cancelar());
    }

    @Test
    void pedidoCanceladoNaoPodeSerPago() {
        assertThrows(IllegalStateException.class, () -> Pedido.novo().cancelar().pagar());
    }

    @Test
    void pedidoCanceladoNaoPodeSerCanceladoNovamente() {
        assertThrows(IllegalStateException.class, () -> Pedido.novo().cancelar().cancelar());
    }
}
