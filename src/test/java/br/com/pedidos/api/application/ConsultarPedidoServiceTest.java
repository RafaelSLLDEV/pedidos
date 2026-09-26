package br.com.pedidos.api.application;

import br.com.pedidos.api.domain.Pedido;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsultarPedidoServiceTest {

    @Test
    void devolvePedidoEncontrado() {
        var id = UUID.randomUUID();
        var esperado = Pedido.novo("c-1");
        var service = new ConsultarPedidoService(new Pedidos() {
            @Override
            public Optional<Pedido> buscarPorId(UUID pedidoId) {
                return Optional.of(esperado);
            }

            @Override
            public Pedido salvar(Pedido pedido) {
                return pedido;
            }
        });

        assertSame(esperado, service.porId(id));
    }

    @Test
    void pedidoInexistenteGeraExcecao() {
        var id = UUID.randomUUID();
        var service = new ConsultarPedidoService(new Pedidos() {
            @Override
            public Optional<Pedido> buscarPorId(UUID pedidoId) {
                return Optional.empty();
            }

            @Override
            public Pedido salvar(Pedido pedido) {
                return pedido;
            }
        });

        assertThrows(PedidoNaoEncontradoException.class, () -> service.porId(id));
    }
}
