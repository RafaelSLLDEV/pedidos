package br.com.pedidos.api.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ItemPedidoTest {

    @Test
    void aceitaQuantidadeEPrecoPositivos() {
        assertDoesNotThrow(() -> new ItemPedido("CAFE-500", 2, new BigDecimal("18.90")));
    }

    @Test
    void recusaQuantidadeZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("CAFE-500", 0, new BigDecimal("18.90")));
    }

    @Test
    void recusaQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("CAFE-500", -1, new BigDecimal("18.90")));
    }

    @Test
    void recusaPrecoZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("CAFE-500", 2, BigDecimal.ZERO));
    }

    @Test
    void recusaPrecoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("CAFE-500", 2, new BigDecimal("-18.90")));
    }
}
