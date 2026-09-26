package br.com.pedidos.api.adapter.entrada.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.pedidos.api.application.CriarPedido;
import br.com.pedidos.api.application.AdicionarItem;
import br.com.pedidos.api.application.PedidoSemItensException;
import br.com.pedidos.api.application.ConsultarPedido;
import br.com.pedidos.api.domain.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;

class PedidoControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new PedidoController(new CriarPedidoFake(), new AdicionarItemFake(), new ConsultarPedidoFake()))
                .setControllerAdvice(new PedidoExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void criaPedidoCom201ETotalDecimal() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("""
                                {"clienteId":"c-1","itens":[{"sku":"CAFE-500","quantidade":2,"precoUnitario":18.90}]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andExpect(jsonPath("$.total").value(37.80));
    }

    @Test
    void quantidadeZeroRetorna422() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("""
                                {"clienteId":"c-1","itens":[{"sku":"CAFE-500","quantidade":0,"precoUnitario":18.90}]}
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void itensVaziosRetornam422() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("{\"clienteId\":\"c-1\",\"itens\":[]}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void jsonMalformadoRetorna400() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("{"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void campoObrigatorioAusenteRetorna400() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("{\"itens\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultaPedidoRetorna200ComItensETotal() throws Exception {
        var id = java.util.UUID.randomUUID();

        mockMvc.perform(get("/pedidos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andExpect(jsonPath("$.total").value(37.80));
    }

    @Test
    void consultaPedidoInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/pedidos/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    private static final class CriarPedidoFake implements CriarPedido {
        private final List<Pedido> pedidos = new ArrayList<>();

        @Override
        public Pedido criar(String cliente, List<br.com.pedidos.api.domain.ItemPedido> itens) {
            if (itens.isEmpty()) {
                throw new PedidoSemItensException("pedido deve conter itens");
            }
            var pedido = Pedido.novo(cliente);
            for (var item : itens) {
                pedido = pedido.adicionarItem(item);
            }
            pedidos.add(pedido);
            return pedido;
        }
    }

    private static final class AdicionarItemFake implements AdicionarItem {
        @Override
        public Pedido executar(java.util.UUID pedidoId, String sku, int quantidade,
                               java.math.BigDecimal precoUnitario) {
            return Pedido.novo("c-1").adicionarItem(new br.com.pedidos.api.domain.ItemPedido(sku, quantidade, precoUnitario));
        }
    }

    private static final class ConsultarPedidoFake implements ConsultarPedido {
        @Override
        public Pedido porId(java.util.UUID pedidoId) {
            if (pedidoId.equals(java.util.UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                throw new br.com.pedidos.api.application.PedidoNaoEncontradoException(pedidoId);
            }
            return new Pedido("c-1", pedidoId, br.com.pedidos.api.domain.StatusPedido.ABERTO,
                    List.of(new br.com.pedidos.api.domain.ItemPedido("CAFE-500", 2,
                            new java.math.BigDecimal("18.90"))));
        }
    }
}
